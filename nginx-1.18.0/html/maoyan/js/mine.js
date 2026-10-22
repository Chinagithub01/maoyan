const { createApp } = Vue;
const CITY_KEY = "maoyan_city";
const AUTH_KEY = "maoyan_session";

createApp({
  data() {
    return {
      cities: [],
      cityId: "",
      session: null,
      orders: [],
      loading: false,
      error: "",
      message: ""
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
    if (this.session) this.loadOrders();
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
      this.orders = [];
    },
    async request(path, options = {}) {
      let response;
      try {
        response = await fetch("/api" + path, options);
      } catch (error) {
        throw new Error("无法连接服务器");
      }
      const result = await response.json().catch(() => ({}));
      if (!response.ok || result.code !== 1) {
        throw new Error(result.msg || "请求失败");
      }
      return result.data;
    },
    async loadCities() {
      try {
        const cities = await this.request("/city");
        this.cities = Array.isArray(cities) ? cities : [];
        const saved = localStorage.getItem(CITY_KEY);
        const current = this.cities.find((city) => String(city.id) === saved)
          || this.cities.find((city) => city.name === "北京")
          || this.cities[0];
        this.cityId = current ? String(current.id) : "";
      } catch (error) {
        this.cities = [];
      }
    },
    async loadOrders() {
      this.loading = true;
      this.error = "";
      try {
        const orders = await this.request("/ticket?userId=" + encodeURIComponent(this.session.id));
        this.orders = (Array.isArray(orders) ? orders : []).map((order) => ({ ...order, confirming: false }));
      } catch (error) {
        this.orders = [];
        this.error = error.message;
      } finally {
        this.loading = false;
      }
    },
    async refund(order) {
      if (!order.confirming) {
        order.confirming = true;
        return;
      }
      this.message = "";
      try {
        await this.request("/ticket/refund", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ userId: this.session.id, orderNo: order.orderNo })
        });
        await this.loadOrders();
      } catch (error) {
        this.message = error.message;
        order.confirming = false;
      }
    },
    formatTime(value) {
      if (!value) return "";
      return String(value).replace("T", " ").slice(0, 16);
    },
    money(value) {
      return "¥" + Number(value || 0).toFixed(2);
    },
    seats(order) {
      const names = (order.tickets || []).map((ticket) => ticket.seatNo).filter(Boolean);
      return names.length ? names.join("、") : "不选座";
    },
    statusText(status) {
      if (status === 2) return "已退票";
      if (status === 0) return "待支付";
      return "已出票";
    }
  }
}).mount("#app");
