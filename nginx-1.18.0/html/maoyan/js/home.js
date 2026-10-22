const { createApp } = Vue;
const CITY_KEY = "maoyan_city";
const AUTH_KEY = "maoyan_session";

createApp({
  data() {
    return {
      cities: [],
      types: [],
      shows: [],
      cityId: "",
      session: null,
      cityError: "",
      typeError: "",
      showError: "",
      loadingTypes: true,
      loadingShows: true
    };
  },
  computed: {
    nickname() {
      if (!this.session) return "";
      return this.session.nickname || this.session.username || "";
    }
  },
  watch: {
    cityId(value) {
      if (value) localStorage.setItem(CITY_KEY, value);
    }
  },
  mounted() {
    this.session = this.readSession();
    this.loadCities();
    this.loadTypes();
    this.loadShows();
  },
  methods: {
    readSession() {
      try {
        return JSON.parse(localStorage.getItem(AUTH_KEY) || "null");
      } catch (error) {
        return null;
      }
    },
    logout() {
      localStorage.removeItem(AUTH_KEY);
      this.session = null;
    },
    async request(path) {
      let response;
      try {
        response = await fetch("/api" + path);
      } catch (error) {
        throw new Error("无法连接服务器");
      }
      const result = await response.json().catch(() => ({}));
      if (!response.ok || result.code !== 1) {
        throw new Error(result.msg || "请求失败");
      }
      return Array.isArray(result.data) ? result.data : [];
    },
    async loadCities() {
      try {
        this.cities = await this.request("/city");
        const saved = localStorage.getItem(CITY_KEY);
        const current = this.cities.find((city) => String(city.id) === saved)
          || this.cities.find((city) => city.name === "北京")
          || this.cities[0];
        this.cityId = current ? String(current.id) : "";
      } catch (error) {
        this.cityError = error.message;
      }
    },
    async loadTypes() {
      this.loadingTypes = true;
      try {
        this.types = await this.request("/show-type");
        this.typeError = "";
      } catch (error) {
        this.types = [];
        this.typeError = error.message;
      } finally {
        this.loadingTypes = false;
      }
    },
    async loadShows() {
      this.loadingShows = true;
      try {
        this.shows = await this.request("/show");
        this.showError = "";
      } catch (error) {
        this.shows = [];
        this.showError = error.message;
      } finally {
        this.loadingShows = false;
      }
    }
  }
}).mount("#app");
