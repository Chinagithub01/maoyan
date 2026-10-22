const AUTH_KEY = "maoyan_session";
const TICKET_KEY = "maoyan_tickets";
const WISH_KEY = "maoyan_wished";
const DRAFT_KEY = "maoyan_draft";
const CITY_KEY = "maoyan_city";

function esc(value) {
  return String(value ?? "").replace(/[&<>"']/g, (char) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;"
  }[char]));
}

function readJson(key, fallback) {
  try {
    const value = JSON.parse(localStorage.getItem(key) || "");
    return value ?? fallback;
  } catch (error) {
    return fallback;
  }
}

function getSession() {
  return readJson(AUTH_KEY, null);
}

function getTickets() {
  return readJson(TICKET_KEY, []);
}

function getWished() {
  return readJson(WISH_KEY, []);
}

function money(value) {
  return "¥" + Number(value).toFixed(2);
}

function formatScore(score) {
  const [whole, fraction] = Number(score).toFixed(1).split(".");
  return `<span class="score"><b>${whole}</b>.${fraction}</span>`;
}

function posterHtml(movie) {
  return `<div class="poster" style="--c1:${movie.c1};--c2:${movie.c2}">
    <span class="poster-tag">${esc(movie.tag)}</span>
    <span class="poster-title">${esc(movie.title)}</span>
  </div>`;
}

let ticketTypes = [];
let cities = [];
let hotShows = [];

async function loadJson(path, failure) {
  let response;
  try {
    response = await fetch("/api" + path);
  } catch (error) {
    throw new Error("无法连接服务器");
  }
  const result = await response.json().catch(() => ({}));
  if (!response.ok || result.code !== 1) {
    throw new Error(result.msg || failure);
  }
  return result.data || [];
}

async function loadCities() {
  if (cities.length) return cities;
  cities = await loadJson("/city", "城市加载失败");
  return cities;
}

function selectedCity(list) {
  const saved = localStorage.getItem(CITY_KEY);
  return list.find((city) => String(city.id) === saved)
    || list.find((city) => city.name === "北京")
    || list[0]
    || null;
}

function rememberCity(city) {
  if (!city) return;
  localStorage.setItem(CITY_KEY, String(city.id));
  const select = document.getElementById("city-select");
  if (select && select.value !== String(city.id)) select.value = String(city.id);
}

function renderCitySelect(list) {
  const select = document.getElementById("city-select");
  if (!select) return;
  const current = selectedCity(list);
  select.innerHTML = list.map((city) => `<option value="${city.id}">${esc(city.name)}</option>`).join("");
  if (current) select.value = String(current.id);
  rememberCity(current);
  select.onchange = () => {
    const city = list.find((item) => String(item.id) === select.value);
    rememberCity(city);
  };
}

async function loadHotShows() {
  if (hotShows.length) return hotShows;
  hotShows = await loadJson("/show", "热卖演出加载失败");
  return hotShows;
}

async function loadTicketTypes() {
  if (ticketTypes.length) return ticketTypes;
  ticketTypes = await loadJson("/show-type", "演出类型加载失败");
  return ticketTypes;
}

function movieCard(movie) {
  const extra = movie.hot
    ? formatScore(movie.score)
    : `<span class="wish">${movie.wish.toLocaleString("zh-CN")} 人想看</span>`;
  const typeId = new URLSearchParams(location.search).get("typeId");
  const query = new URLSearchParams({ id: String(movie.id) });
  if (typeId) query.set("typeId", typeId);
  return `<a class="movie-card" href="detail.html?${query.toString()}">
    ${posterHtml(movie)}
    <div class="movie-body">
      <h3>${esc(movie.title)}</h3>
      <p class="meta">${esc(movie.category)} / ${movie.duration}分钟</p>
      ${extra}
      <span class="${movie.hot ? "buy" : "buy ghost"}">${movie.hot ? "购票" : "预售"}</span>
    </div>
  </a>`;
}

function upcomingDates(count) {
  const weeks = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
  const dates = [];
  for (let i = 0; i < count; i += 1) {
    const date = new Date();
    date.setDate(date.getDate() + i);
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    dates.push({
      value: `${date.getFullYear()}-${month}-${day}`,
      label: i === 0 ? "今天" : i === 1 ? "明天" : `${Number(month)}月${Number(day)}日`,
      week: weeks[date.getDay()]
    });
  }
  return dates;
}

function hallName() {
  return "1号厅";
}

function safeRedirect(value) {
  if (!value || value.startsWith("http") || value.startsWith("//")) return "mine.html";
  if (!/^(index|detail|seat|mine|tickets)\.html(\?[^#]*)?(#.*)?$/.test(value)) return "mine.html";
  return value;
}

function saveSession(user) {
  localStorage.setItem(AUTH_KEY, JSON.stringify({
    id: user.id,
    username: user.username,
    nickname: user.nickname,
    phone: user.phone
  }));
}

async function requestUser(path, body) {
  let response;
  try {
    response = await fetch("/api/user/" + path, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });
  } catch (error) {
    throw new Error("无法连接服务器");
  }
  const result = await response.json().catch(() => ({}));
  if (!response.ok || result.code !== 1) {
    throw new Error(result.msg || "请求失败");
  }
  return result.data;
}

function renderLayout() {
  const session = getSession();
  const page = document.body.dataset.page || "";
  const current = page === "tickets" ? "mine" : page;
  const links = [
    ["home", "index.html", "首页"],
    ["mine", "mine.html", "我的"]
  ];
  const nav = links.map(([key, href, label]) =>
    `<a href="${href}" class="${current === key ? "active" : ""}">${label}</a>`
  ).join("");
  const account = session
    ? `<span class="nick">${esc(session.nickname || session.username)}</span><button type="button" id="logout-btn" class="linkish">退出</button>`
    : `<a href="login.html">登录</a><a href="login.html?tab=register">注册</a>`;

  document.getElementById("header").innerHTML = `
    <div class="topbar">
      <div class="container topbar-inner">
        <select id="city-select" class="city-select" aria-label="城市"></select>
        <div class="topbar-links">${account}</div>
      </div>
    </div>
    <header class="navbar">
      <div class="container navbar-inner">
        <a class="brand" href="index.html"><span class="logo-mark">猫</span><span>猫眼演出</span></a>
        <nav class="nav">${nav}</nav>
      </div>
    </header>`;

  document.getElementById("footer").innerHTML = `
    <footer class="footer">
      <div class="container">
        <p>猫眼演出</p>
        <p>演出、城市和类型来自数据库。</p>
      </div>
    </footer>`;

  const logout = document.getElementById("logout-btn");
  if (logout) {
    logout.addEventListener("click", () => {
      localStorage.removeItem(AUTH_KEY);
      location.href = "index.html";
    });
  }
  loadCities().then(renderCitySelect).catch(() => {});
}
