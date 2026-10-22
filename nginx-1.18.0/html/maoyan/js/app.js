function movieById(id) {
  return MOVIES.find((movie) => movie.id === Number(id));
}

function hotCard(show) {
  const artist = show.artist ? `${esc(show.artist)} / ` : "";
  const cover = show.poster
    ? `<img class="show-cover" src="${esc(show.poster)}" alt="${esc(show.name)}">`
    : "";
  return `<article class="movie-card">
    ${cover}
    <div class="movie-body">
      <h3>${esc(show.name)}</h3>
      <p class="meta">${esc(show.showTypeName)} / ${esc(show.cityName)}</p>
      <p class="meta">${artist}${esc(show.venueName)}</p>
      <span class="wish">已售 ${Number(show.soldCount || 0)} 张</span>
    </div>
  </article>`;
}

function renderHome() {
  const typeRow = document.getElementById("type-row");
  const hotList = document.getElementById("hot-list");
  typeRow.innerHTML = `<p class="meta">正在加载演出类型</p>`;
  hotList.innerHTML = `<p class="meta">正在加载热卖演出</p>`;

  loadTicketTypes().then((types) => {
    typeRow.innerHTML = types.length
      ? types.map((type) => `<div class="type-card"><strong>${esc(type.name)}</strong></div>`).join("")
      : `<p class="empty">暂无演出类型</p>`;
  }).catch((error) => {
    typeRow.innerHTML = `<p class="empty">${esc(error.message)}</p>`;
  });

  loadHotShows().then((shows) => {
    hotList.innerHTML = shows.length
      ? shows.map(hotCard).join("")
      : `<p class="empty">暂无热卖演出</p>`;
  }).catch((error) => {
    hotList.innerHTML = `<p class="empty">${esc(error.message)}</p>`;
  });
}

function renderMovies() {
  const params = new URLSearchParams(location.search);
  const typeId = params.get("typeId");
  const title = document.querySelector(".page-head h1");
  if (typeId && title) {
    loadTicketTypes().then((types) => {
      const type = types.find((item) => String(item.id) === String(typeId));
      if (!type) return;
      title.textContent = type.name;
      document.title = type.name + " - 猫眼演出";
    }).catch(() => {});
  }
  let tab = params.get("tab") === "soon" ? "soon" : "hot";
  let category = CATEGORIES.includes(params.get("cat")) ? params.get("cat") : "全部";
  const keyword = (params.get("q") || "").trim();
  const tabs = document.getElementById("movie-tabs");
  const chips = document.getElementById("cat-chips");
  const list = document.getElementById("movie-list");
  const tip = document.getElementById("result-tip");

  chips.innerHTML = CATEGORIES.map((name) =>
    `<button type="button" data-cat="${esc(name)}">${esc(name)}</button>`
  ).join("");

  function paint() {
    tabs.querySelectorAll("button").forEach((button) => {
      button.classList.toggle("active", button.dataset.tab === tab);
    });
    chips.querySelectorAll("button").forEach((button) => {
      button.classList.toggle("active", button.dataset.cat === category);
    });
    const items = MOVIES.filter((movie) => {
      const tabOk = tab === "hot" ? movie.hot : !movie.hot;
      const categoryOk = category === "全部" || movie.category === category;
      const keywordOk = !keyword || [movie.title, movie.actors, movie.director, movie.category]
        .some((field) => field.includes(keyword));
      return tabOk && categoryOk && keywordOk;
    });
    tip.textContent = keyword ? `“${keyword}” 共 ${items.length} 部` : `共 ${items.length} 部`;
    list.innerHTML = items.length
      ? items.map(movieCard).join("")
      : `<p class="empty">没有找到相关演出</p>`;
  }

  tabs.addEventListener("click", (event) => {
    const button = event.target.closest("button");
    if (!button) return;
    tab = button.dataset.tab;
    paint();
  });
  chips.addEventListener("click", (event) => {
    const button = event.target.closest("button");
    if (!button) return;
    category = button.dataset.cat;
    paint();
  });
  paint();
}

function renderDetail() {
  const root = document.getElementById("detail-root");
  const movie = movieById(new URLSearchParams(location.search).get("id"));
  if (!movie) {
    root.innerHTML = `<p class="empty">没有找到这场演出，<a href="index.html">返回首页</a></p>`;
    return;
  }
  document.title = movie.title + " - 猫眼演出";
  const score = movie.hot ? formatScore(movie.score) : `<span class="wish">${movie.wish.toLocaleString("zh-CN")} 人想看</span>`;
  root.innerHTML = `
    <article class="detail-hero">
      ${posterHtml(movie)}
      <div class="detail-copy">
        <h1>${esc(movie.title)}</h1>
        <p>${score}</p>
        <p class="kv">类型：${esc(movie.category)}<br>时长：${movie.duration}分钟<br>地区：${esc(movie.region)}<br>上映：${esc(movie.release)}<br>导演：${esc(movie.director)}<br>主演：${esc(movie.actors)}<br>版本：${esc(movie.formats.join(" / "))}</p>
        <p class="summary">${esc(movie.summary)}</p>
      </div>
    </article>
    <section class="panel" id="sessions">
      <h2>${movie.hot ? "选座购票" : "即将上映"}</h2>
      <div id="session-body"></div>
    </section>`;
  const typeId = new URLSearchParams(location.search).get("typeId");
  if (movie.hot && typeId) {
    loadTicketTypes().then((types) => {
      const type = types.find((item) => String(item.id) === String(typeId));
      const heading = document.querySelector("#sessions h2");
      if (type && heading) heading.textContent = `选座购票 · ${type.name}`;
    }).catch(() => {});
  }
  if (!movie.hot) {
    renderWish(movie);
    return;
  }
  renderShowtimes(movie);
}

function renderWish(movie) {
  const body = document.getElementById("session-body");
  const wished = getWished().includes(movie.id);
  body.innerHTML = `<p>${esc(movie.release)} 上映，场次尚未开放。</p>
    <button type="button" id="wish-btn" class="${wished ? "buy ghost" : "buy"}">${wished ? "已想看" : "想看"}</button>`;
  document.getElementById("wish-btn").addEventListener("click", () => {
    const current = new Set(getWished());
    if (current.has(movie.id)) current.delete(movie.id);
    else current.add(movie.id);
    localStorage.setItem(WISH_KEY, JSON.stringify([...current]));
    renderWish(movie);
  });
}

function renderShowtimes(movie) {
  const dates = upcomingDates(4);
  let dateIndex = 0;
  const body = document.getElementById("session-body");

  function paint() {
    const dateTabs = dates.map((date, index) =>
      `<button type="button" data-date="${index}" class="${index === dateIndex ? "active" : ""}">${date.label} ${date.week}</button>`
    ).join("");
    const cinemas = CINEMAS.map((cinema, cinemaIndex) => {
      const times = SHOW_TIMES.map((time, timeIndex) => {
        const closed = (cinemaIndex + timeIndex + dateIndex) % 7 === 0;
        if (closed) return `<span class="time-btn disabled">${time} 满座</span>`;
        const query = {
          movieId: String(movie.id),
          cinema: cinema.name,
          date: dates[dateIndex].value,
          time
        };
        const typeId = new URLSearchParams(location.search).get("typeId");
        if (typeId) query.typeId = typeId;
        const href = "seat.html?" + new URLSearchParams(query).toString();
        return `<a class="time-btn" href="${href}">${time}</a>`;
      }).join("");
      return `<article class="cinema"><h3>${esc(cinema.name)}</h3><p class="addr">${esc(cinema.address)}</p><div class="times">${times}</div></article>`;
    }).join("");
    body.innerHTML = `<div class="tabs" id="date-tabs">${dateTabs}</div>${cinemas}`;
    document.getElementById("date-tabs").addEventListener("click", (event) => {
      const button = event.target.closest("button");
      if (!button) return;
      dateIndex = Number(button.dataset.date);
      paint();
    });
  }

  paint();
}

function seatClosed(row, col, movieId) {
  return (row * 5 + col * 3 + Number(movieId) * 7) % 10 < 3;
}

function renderSeat() {
  const root = document.getElementById("seat-root");
  const params = new URLSearchParams(location.search);
  const movie = movieById(params.get("movieId"));
  const cinema = CINEMAS.find((item) => item.name === params.get("cinema"));
  const date = params.get("date") || "";
  const time = params.get("time") || "";
  const validWhen = /^\d{4}-\d{2}-\d{2}$/.test(date) && /^\d{2}:\d{2}$/.test(time);
  if (!movie || !movie.hot || !cinema || !validWhen) {
    root.innerHTML = `<p class="empty">场次不存在，<a href="index.html">返回首页</a></p>`;
    return;
  }

  root.innerHTML = `<p class="meta">正在加载票种</p>`;
  loadTicketTypes().then((types) => {
  if (!types.length) {
    root.innerHTML = `<p class="empty">暂无票种，<a href="index.html">返回首页</a></p>`;
    return;
  }
  const selected = new Set();
  let typeId = types[0].id;
  const requested = Number(params.get("typeId"));
  if (types.some((type) => type.id === requested)) typeId = requested;
  let draft = null;
  try {
    draft = JSON.parse(sessionStorage.getItem(DRAFT_KEY) || "null");
  } catch (error) {
    draft = null;
  }
  if (draft && draft.movieId === movie.id && draft.cinema === cinema.name && draft.date === date && draft.time === time) {
    draft.seats.forEach((seat) => {
      const matched = /^(\d+)排(\d+)座$/.exec(seat);
      if (!matched || seatClosed(Number(matched[1]), Number(matched[2]), movie.id)) return;
      selected.add(seat);
    });
    if (types.some((type) => type.id === draft.typeId)) typeId = draft.typeId;
  }

  root.innerHTML = `
    <div class="steps"><span>1 演出</span><span>2 场次</span><span class="on">3 选座</span><span>4 出票</span></div>
    <div class="seat-layout">
      <section class="seat-board">
        <h2>${esc(movie.title)}</h2>
        <p class="meta" id="show-info"></p>
        <div class="screen">银幕中央</div>
        <div id="seat-map"></div>
        <div class="legend"><span><i></i>可选</span><span><i class="picked-box"></i>已选</span><span><i class="sold-box"></i>已售</span></div>
      </section>
      <aside class="summary-panel">
        <h2>确认订单</h2>
        <p>${esc(cinema.name)}</p>
        <div class="type-picks" id="type-picks"></div>
        <div class="selected-seats" id="selected-seats"></div>
        <div class="total" id="total">${money(0)}</div>
        <button type="button" class="full-btn" id="confirm-btn" disabled>确认选座</button>
        <p class="msg" id="seat-msg"></p>
      </aside>
    </div>`;

  function currentType() {
    return types.find((type) => type.id === typeId);
  }

  function paint() {
    const type = currentType();
    document.getElementById("show-info").textContent = `${date} ${time} · ${type.name} · ${hallName()} · ${cinema.address}`;
    document.getElementById("type-picks").innerHTML = types.map((item) =>
      `<button type="button" class="type-option ${item.id === typeId ? "active" : ""}" data-type="${item.id}">
        <span>${esc(item.name)}</span>
      </button>`
    ).join("");
    const rows = [];
    for (let row = 1; row <= 8; row += 1) {
      const seats = [];
      for (let col = 1; col <= 12; col += 1) {
        if (col === 7) seats.push(`<span class="aisle"></span>`);
        const label = `${row}排${col}座`;
        const sold = seatClosed(row, col, movie.id);
        const picked = selected.has(label);
        seats.push(`<button type="button" class="seat ${sold ? "sold" : ""} ${picked ? "picked" : ""}" data-seat="${label}" ${sold ? "disabled" : ""} aria-label="${label}"></button>`);
      }
      rows.push(`<div class="seat-row"><span class="row-no">${row}排</span>${seats.join("")}</div>`);
    }
    document.getElementById("seat-map").innerHTML = rows.join("");
    const labels = [...selected];
    document.getElementById("selected-seats").innerHTML = labels.length
      ? labels.map((seat) => `<span class="seat-tag">${esc(seat)}</span>`).join("")
      : `<span class="meta">最多选择 4 个座位</span>`;
    document.getElementById("total").textContent = labels.length ? `已选 ${labels.length} 张 · ${type.name}` : type.name;
    document.getElementById("confirm-btn").disabled = labels.length === 0;
  }

  root.addEventListener("click", (event) => {
    const typeButton = event.target.closest(".type-option");
    if (typeButton) {
      typeId = Number(typeButton.dataset.type);
      paint();
      return;
    }
    const seatButton = event.target.closest(".seat");
    if (!seatButton || seatButton.disabled) return;
    const label = seatButton.dataset.seat;
    if (selected.has(label)) selected.delete(label);
    else if (selected.size >= 4) {
      document.getElementById("seat-msg").textContent = "一次最多选择 4 个座位";
      return;
    } else selected.add(label);
    document.getElementById("seat-msg").textContent = "";
    paint();
  });

  document.getElementById("confirm-btn").addEventListener("click", () => {
    if (selected.size === 0) return;
    const type = currentType();
    const order = {
      movieId: movie.id,
      movieName: movie.title,
      cinema: cinema.name,
      date,
      time,
      showTime: `${date} ${time}:00`,
      typeId: type.id,
      typeName: type.name,
      unitPrice: null,
      hall: hallName(),
      seats: [...selected]
    };
    const session = getSession();
    if (!session) {
      sessionStorage.setItem(DRAFT_KEY, JSON.stringify(order));
      const back = "seat.html" + location.search;
      location.href = "login.html?redirect=" + encodeURIComponent(back);
      return;
    }
    const tickets = getTickets();
    const orderNo = "MY" + Date.now();
    const createTime = new Date().toISOString().slice(0, 19).replace("T", " ");
    order.seats.forEach((seat, index) => {
      tickets.unshift({
        id: Date.now() + index,
        orderNo,
        userId: session.id,
        ticketTypeId: order.typeId,
        ticketTypeName: order.typeName,
        movieName: order.movieName,
        cinemaName: order.cinema,
        hallName: order.hall,
        showTime: order.showTime,
        seatNo: seat,
        price: order.unitPrice,
        status: 1,
        createTime
      });
    });
    localStorage.setItem(TICKET_KEY, JSON.stringify(tickets));
    sessionStorage.removeItem(DRAFT_KEY);
    location.href = "tickets.html?created=1";
  });
  paint();
  }).catch((error) => {
    root.innerHTML = `<p class="empty">${esc(error.message)}</p>`;
  });
}

function renderLogin() {
  const params = new URLSearchParams(location.search);
  const redirect = safeRedirect(params.get("redirect"));
  const loginForm = document.getElementById("login-form");
  const registerForm = document.getElementById("register-form");
  const message = document.getElementById("auth-msg");
  const session = getSession();
  if (session) {
    document.getElementById("auth-card").innerHTML = `
      <h1>已登录</h1>
      <p>${esc(session.nickname || session.username)}，可以继续购票或查看订单。</p>
      <p><a href="${esc(redirect)}">返回上一页</a></p>`;
    return;
  }

  function show(tab) {
    const register = tab === "register";
    loginForm.hidden = register;
    registerForm.hidden = !register;
    document.querySelectorAll("#auth-tabs button").forEach((button) => {
      button.classList.toggle("active", button.dataset.tab === (register ? "register" : "login"));
    });
    message.textContent = "";
  }

  document.getElementById("auth-tabs").addEventListener("click", (event) => {
    const button = event.target.closest("button");
    if (button) show(button.dataset.tab);
  });
  show(params.get("tab") === "register" ? "register" : "login");

  loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const username = new FormData(loginForm).get("username").trim();
    const password = new FormData(loginForm).get("password");
    message.className = "msg";
    try {
      const user = await requestUser("login", { username, password });
      saveSession(user);
      location.href = redirect;
    } catch (error) {
      message.textContent = error.message;
    }
  });

  registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = new FormData(registerForm);
    const username = String(form.get("username") || "").trim();
    const password = String(form.get("password") || "");
    const phone = String(form.get("phone") || "").trim();
    const nickname = String(form.get("nickname") || "").trim();
    message.className = "msg";
    try {
      const user = await requestUser("register", { username, password, phone, nickname });
      saveSession(user);
      location.href = redirect;
    } catch (error) {
      message.textContent = error.message;
    }
  });
}

function renderMine() {
  const root = document.getElementById("mine-root");
  const session = getSession();
  if (!session) {
    root.innerHTML = `
      <section class="panel mine-card">
        <h2>未登录</h2>
        <p>登录后可以查看订单。</p>
        <div class="banner-actions">
          <a class="buy" href="login.html?redirect=mine.html">登录</a>
          <a class="buy ghost" href="login.html?tab=register&redirect=mine.html">注册</a>
        </div>
      </section>
      <a class="menu-row" href="login.html?redirect=tickets.html">订单<span>登录后查看</span></a>`;
    return;
  }
  root.innerHTML = `
    <section class="panel mine-card">
      <h2>${esc(session.nickname || session.username)}</h2>
      <p class="meta">用户名 ${esc(session.username)} · ${esc(session.phone || "")}</p>
    </section>
    <a class="menu-row" href="tickets.html">订单<span>查看订单</span></a>`;
}

function renderTickets() {
  const tip = document.getElementById("mine-tip");
  const list = document.getElementById("mine-list");
  const session = getSession();
  if (!session) {
    tip.textContent = "";
    list.innerHTML = `<p class="empty">登录后可以查看订单。<a href="login.html?redirect=tickets.html">去登录</a></p>`;
    return;
  }
  const created = new URLSearchParams(location.search).get("created") === "1";
  tip.className = created ? "msg ok" : "result-tip";
  tip.textContent = created ? "出票成功。" : `${session.nickname || session.username} 的订单`;
  const mine = getTickets().filter((ticket) => ticket.userId === session.id);
  if (!mine.length) {
    list.innerHTML = `<p class="empty">还没有订单，<a href="index.html">去首页看看</a></p>`;
    return;
  }
  const groups = new Map();
  mine.forEach((ticket) => {
    if (!groups.has(ticket.orderNo)) groups.set(ticket.orderNo, []);
    groups.get(ticket.orderNo).push(ticket);
  });
  list.className = "ticket-list";
  list.innerHTML = [...groups.values()].map((tickets) => {
    const first = tickets[0];
    const refunded = tickets.every((ticket) => ticket.status === 2);
    const seats = tickets.map((ticket) => ticket.seatNo).join("、");
    const priced = tickets.every((ticket) => ticket.price != null && ticket.price !== "");
    const total = tickets.reduce((sum, ticket) => sum + Number(ticket.price || 0), 0);
    return `<article class="ticket-card">
      <div class="ticket-main">
        <h3>${esc(first.movieName)}</h3>
        <p>${esc(first.cinemaName)} · ${esc(first.hallName)}</p>
        <p>${esc(first.showTime)} · ${esc(first.ticketTypeName)}</p>
        <p>座位：${esc(seats)}</p>
        <p class="meta">订单号 ${esc(first.orderNo)}</p>
      </div>
      <div class="ticket-stub">
        <span class="price">${priced ? money(total) : esc(first.ticketTypeName)}</span>
        <span class="status ${refunded ? "back" : "done"}">${refunded ? "已退票" : "已出票"}</span>
        ${refunded ? "" : `<button type="button" class="buy ghost" data-order="${esc(first.orderNo)}">退票</button>`}
      </div>
    </article>`;
  }).join("");

  list.onclick = (event) => {
    const button = event.target.closest("button[data-order]");
    if (!button) return;
    const orderNo = button.dataset.order;
    if (button.dataset.confirm !== "1") {
      button.dataset.confirm = "1";
      button.textContent = "确认退票";
      return;
    }
    const next = getTickets().map((ticket) =>
      ticket.orderNo === orderNo && ticket.userId === session.id ? { ...ticket, status: 2 } : ticket
    );
    localStorage.setItem(TICKET_KEY, JSON.stringify(next));
    history.replaceState(null, "", "tickets.html");
    renderTickets();
  };
}

document.addEventListener("DOMContentLoaded", () => {
  renderLayout();
  const page = document.body.dataset.page;
  if (page === "home") renderHome();
  if (page === "detail") renderDetail();
  if (page === "seat") renderSeat();
  if (page === "login") renderLogin();
  if (page === "mine") renderMine();
  if (page === "tickets") renderTickets();
});
