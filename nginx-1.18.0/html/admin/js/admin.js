const TOKEN_KEY = "maoyan_admin_token";
const NAME_KEY = "maoyan_admin_name";

const loginView = document.getElementById("login-view");
const consoleView = document.getElementById("console-view");
const loginError = document.getElementById("login-error");
const typeBody = document.getElementById("type-body");
const showBody = document.getElementById("show-body");
const typeError = document.getElementById("type-error");
const showError = document.getElementById("show-error");
const editor = document.getElementById("editor");
const editorFields = document.getElementById("editor-fields");
const editorError = document.getElementById("editor-error");
const editorTitle = document.getElementById("editor-title");

let types = [];
let shows = [];
let editorMode = null;

function esc(value) {
  return String(value ?? "").replace(/[&<>"']/g, (char) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  }[char]));
}

async function api(path, options = {}) {
  const headers = { ...(options.headers || {}) };
  const token = sessionStorage.getItem(TOKEN_KEY);
  if (token) headers.Authorization = "Bearer " + token;
  if (options.body && !(options.body instanceof FormData)) headers["Content-Type"] = "application/json";
  let response;
  try {
    response = await fetch("/api" + path, { ...options, headers });
  } catch (error) {
    throw new Error("无法连接服务器");
  }
  const result = await response.json().catch(() => ({}));
  if (result.code !== 1) {
    if (result.msg === "请先登录") showLogin();
    throw new Error(result.msg || "请求失败");
  }
  return result.data;
}

async function onPosterFile(event) {
  const file = event.target.files && event.target.files[0];
  const status = document.getElementById("poster-status");
  const preview = document.getElementById("poster-preview");
  const hidden = event.target.parentElement.querySelector("input[name=poster]");
  if (!file) return;
  status.textContent = "正在上传，浏览器下载后再传到阿里云";
  try {
    const form = new FormData();
    form.append("file", file);
    const uploaded = await api("/admin/show/poster", { method: "POST", body: form });
    const token = sessionStorage.getItem(TOKEN_KEY);
    const downloaded = await fetch("/api/admin/show/poster/" + uploaded.token, {
      headers: { Authorization: "Bearer " + token }
    });
    const type = downloaded.headers.get("content-type") || "";
    if (type.includes("application/json")) {
      const result = await downloaded.json();
      throw new Error(result.msg || "海报文件已失效，请重新上传");
    }
    const blob = await downloaded.blob();
    const ossForm = new FormData();
    ossForm.append("file", new File([blob], uploaded.fileName || file.name, { type: blob.type || file.type }));
    ossForm.append("token", uploaded.token);
    const stored = await api("/admin/show/poster/oss", { method: "POST", body: ossForm });
    hidden.value = stored.url;
    preview.src = stored.url;
    preview.classList.remove("hidden");
    status.textContent = "已上传到阿里云";
  } catch (error) {
    status.textContent = error.message;
  }
}

function showLogin() {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(NAME_KEY);
  loginView.classList.remove("hidden");
  consoleView.classList.add("hidden");
}

function showConsole() {
  loginView.classList.add("hidden");
  consoleView.classList.remove("hidden");
  document.getElementById("admin-name").textContent = sessionStorage.getItem(NAME_KEY) || "管理员";
  loadTypes();
  loadShows();
}

document.getElementById("login-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  loginError.textContent = "";
  try {
    const data = await api("/admin/login", {
      method: "POST",
      body: JSON.stringify({
        username: document.getElementById("login-username").value.trim(),
        password: document.getElementById("login-password").value
      })
    });
    sessionStorage.setItem(TOKEN_KEY, data.token);
    sessionStorage.setItem(NAME_KEY, data.nickname || data.username);
    showConsole();
  } catch (error) {
    loginError.textContent = error.message;
  }
});

document.getElementById("logout-btn").addEventListener("click", () => showLogin());

document.querySelectorAll(".nav-btn").forEach((button) => {
  button.addEventListener("click", () => {
    document.querySelectorAll(".nav-btn").forEach((item) => item.classList.toggle("active", item === button));
    const showType = button.dataset.panel === "show-type";
    document.getElementById("panel-show-type").classList.toggle("hidden", !showType);
    document.getElementById("panel-show").classList.toggle("hidden", showType);
    document.getElementById("panel-title").textContent = showType ? "演出类型" : "演出";
  });
});

async function loadTypes() {
  typeError.textContent = "";
  try {
    types = await api("/admin/showType");
    typeBody.innerHTML = types.map((type) => `<tr>
      <td>${type.id}</td>
      <td>${esc(type.name)}</td>
      <td>
        <button type="button" class="ghost" data-edit-type="${type.id}">修改</button>
        <button type="button" data-delete-type="${type.id}">删除</button>
      </td>
    </tr>`).join("");
  } catch (error) {
    typeError.textContent = error.message;
  }
}

async function loadShows() {
  showError.textContent = "";
  try {
    shows = await api("/admin/show");
    showBody.innerHTML = shows.map((show) => `<tr>
      <td>${show.id}</td>
      <td>${esc(show.name)}</td>
      <td>${esc(typeName(show.showTypeId))}</td>
      <td>${show.cityId}</td>
      <td>${esc(show.venueName)}</td>
      <td>${show.stopped === 1 ? "是" : "否"}</td>
      <td>
        <button type="button" class="ghost" data-edit-show="${show.id}">修改</button>
        <button type="button" data-delete-show="${show.id}">删除</button>
      </td>
    </tr>`).join("");
  } catch (error) {
    showError.textContent = error.message;
  }
}

function typeName(id) {
  return types.find((type) => type.id === id)?.name || id;
}

document.getElementById("add-type").addEventListener("click", () => openType());
document.getElementById("add-show").addEventListener("click", () => openShow());
typeBody.addEventListener("click", onTypeClick);
showBody.addEventListener("click", onShowClick);
document.getElementById("editor-cancel").addEventListener("click", () => editor.close());
document.getElementById("editor-form").addEventListener("submit", saveEditor);

function onTypeClick(event) {
  const edit = event.target.closest("[data-edit-type]");
  const remove = event.target.closest("[data-delete-type]");
  if (edit) openType(types.find((type) => type.id === Number(edit.dataset.editType)));
  if (remove) removeType(Number(remove.dataset.deleteType));
}

function onShowClick(event) {
  const edit = event.target.closest("[data-edit-show]");
  const remove = event.target.closest("[data-delete-show]");
  if (edit) openShow(shows.find((show) => show.id === Number(edit.dataset.editShow)));
  if (remove) removeShow(Number(remove.dataset.deleteShow));
}

function openType(type) {
  editorMode = { kind: "type", id: type?.id };
  editorTitle.textContent = type ? "修改演出类型" : "新增演出类型";
  editorFields.innerHTML = `<label class="wide">名称<input name="name" maxlength="50" value="${esc(type?.name || "")}" required></label>`;
  editorError.textContent = "";
  editor.showModal();
}

function openShow(show) {
  editorMode = { kind: "show", id: show?.id };
  editorTitle.textContent = show ? "修改演出" : "新增演出";
  const typeOptions = types.map((type) =>
    `<option value="${type.id}" ${show?.showTypeId === type.id ? "selected" : ""}>${esc(type.name)}</option>`
  ).join("");
  editorFields.innerHTML = `
    <label>演出类型<select name="showTypeId">${typeOptions}</select></label>
    <label>城市编号<input name="cityId" type="number" min="1" value="${show?.cityId ?? ""}" required></label>
    <label class="wide">名称<input name="name" maxlength="100" value="${esc(show?.name || "")}" required></label>
    <label>艺人<input name="artist" maxlength="100" value="${esc(show?.artist || "")}"></label>
    <label>场馆<input name="venueName" maxlength="100" value="${esc(show?.venueName || "")}" required></label>
    <label class="wide">海报
      <input id="poster-file" type="file" accept="image/jpeg,image/png,image/webp,image/gif">
      <input type="hidden" name="poster" value="${esc(show?.poster || "")}">
      <img id="poster-preview" class="poster-preview${show?.poster ? "" : " hidden"}" alt="海报预览" src="${esc(show?.poster || "")}">
      <span id="poster-status" class="hint"></span>
    </label>
    <label class="wide">地址<input name="address" maxlength="200" value="${esc(show?.address || "")}"></label>
    <label>时长（分钟）<input name="duration" type="number" min="1" value="${show?.duration ?? ""}"></label>
    <label>限购<input name="maxPerOrder" type="number" min="1" max="6" value="${show?.maxPerOrder ?? 6}"></label>
    <label>可选座<select name="seatEnabled">${flagOptions(show?.seatEnabled)}</select></label>
    <label>可退<select name="refundEnabled">${flagOptions(show?.refundEnabled)}</select></label>
    <label>停售<select name="stopped">${flagOptions(show?.stopped)}</select></label>
    <label>取票方式<input name="pickupType" maxlength="20" value="${esc(show?.pickupType || "电子票")}"></label>
    <label>开售时间<input name="saleStartTime" type="datetime-local" value="${dateValue(show?.saleStartTime)}"></label>
    <label>停售时间<input name="saleEndTime" type="datetime-local" value="${dateValue(show?.saleEndTime)}"></label>
    <label class="wide">介绍<textarea name="intro" rows="3">${esc(show?.intro || "")}</textarea></label>
    <label class="wide">购票须知<textarea name="notice" rows="3">${esc(show?.notice || "")}</textarea></label>`;
  editorError.textContent = "";
  editor.showModal();
  document.getElementById("poster-file").addEventListener("change", onPosterFile);
}

function flagOptions(value) {
  const current = value == null ? 0 : value;
  return `<option value="0" ${current === 0 ? "selected" : ""}>否</option><option value="1" ${current === 1 ? "selected" : ""}>是</option>`;
}

function dateValue(value) {
  return value ? String(value).slice(0, 16) : "";
}

async function removeType(id) {
  const type = types.find((item) => item.id === id);
  if (!window.confirm(`删除演出类型「${type?.name || id}」？`)) return;
  typeError.textContent = "";
  try {
    await api("/admin/showType/" + id, { method: "DELETE" });
    await loadTypes();
  } catch (error) {
    typeError.textContent = error.message;
  }
}

async function removeShow(id) {
  const show = shows.find((item) => item.id === id);
  if (!window.confirm(`删除演出「${show?.name || id}」？`)) return;
  showError.textContent = "";
  try {
    await api("/admin/show/" + id, { method: "DELETE" });
    await loadShows();
  } catch (error) {
    showError.textContent = error.message;
  }
}

async function saveEditor(event) {
  event.preventDefault();
  editorError.textContent = "";
  const form = new FormData(event.currentTarget);
  try {
    if (editorMode.kind === "type") {
      const body = JSON.stringify({ name: String(form.get("name") || "").trim() });
      if (editorMode.id) await api("/admin/showType/" + editorMode.id, { method: "PUT", body });
      else await api("/admin/showType", { method: "POST", body });
      editor.close();
      await loadTypes();
      await loadShows();
      return;
    }
    const body = JSON.stringify(showPayload(form));
    if (editorMode.id) await api("/admin/show/" + editorMode.id, { method: "PUT", body });
    else await api("/admin/show", { method: "POST", body });
    editor.close();
    await loadShows();
  } catch (error) {
    editorError.textContent = error.message;
  }
}

function showPayload(form) {
  const text = (name) => {
    const value = String(form.get(name) || "").trim();
    return value || null;
  };
  const time = (name) => {
    const value = String(form.get(name) || "");
    return value ? value + ":00" : null;
  };
  const numberOrNull = (name) => {
    const value = String(form.get(name) || "").trim();
    return value ? Number(value) : null;
  };
  return {
    showTypeId: Number(form.get("showTypeId")),
    cityId: Number(form.get("cityId")),
    name: text("name"),
    artist: text("artist"),
    poster: text("poster"),
    address: text("address"),
    venueName: text("venueName"),
    duration: numberOrNull("duration"),
    maxPerOrder: numberOrNull("maxPerOrder"),
    seatEnabled: Number(form.get("seatEnabled")),
    refundEnabled: Number(form.get("refundEnabled")),
    stopped: Number(form.get("stopped")),
    pickupType: text("pickupType"),
    saleStartTime: time("saleStartTime"),
    saleEndTime: time("saleEndTime"),
    intro: text("intro"),
    notice: text("notice")
  };
}

if (sessionStorage.getItem(TOKEN_KEY)) showConsole();
