const CATEGORIES = ["全部", "科幻", "剧情", "喜剧", "悬疑", "动画", "爱情"];

const CINEMAS = [
  { name: "星海国际影城（湖滨店）", address: "上城区湖滨路 88 号 5 楼" },
  { name: "光影天地影城（城西店）", address: "西湖区文三路 168 号 4 楼" },
  { name: "橙光激光影城（钱江店）", address: "滨江区江虹路 66 号 6 楼" }
];

const SHOW_TIMES = ["10:20", "13:00", "15:40", "18:20", "21:00"];

const MOVIES = [
  {
    id: 1, title: "深海回声", score: 9.4, category: "科幻", duration: 142,
    region: "中国大陆", release: "2026-10-01", director: "林昭",
    actors: "陈屿 / 周晚宁 / 宋北", hot: true, wish: 0, tag: "IMAX",
    formats: ["2D", "3D", "IMAX"], c1: "#0e3a4d", c2: "#e85d4c",
    summary: "近海实验站收到一段来自深海的回声。调查组下潜后发现，回声里录着他们还没有发生的对话。"
  },
  {
    id: 2, title: "灯塔来信", score: 9.1, category: "剧情", duration: 126,
    region: "中国大陆", release: "2026-10-08", director: "许澄",
    actors: "江禾 / 沈予安", hot: true, wish: 0, tag: "热映",
    formats: ["2D", "3D"], c1: "#3d2b1f", c2: "#d4a017",
    summary: "守塔人每隔七年会收到一封没有寄件人的信。最后一封信寄到的那天，灯塔外面的海平线消失了。"
  },
  {
    id: 3, title: "夜航船", score: 8.8, category: "悬疑", duration: 118,
    region: "中国大陆", release: "2026-10-15", director: "裴川",
    actors: "顾南 / 梁小满", hot: true, wish: 0, tag: "热映",
    formats: ["2D"], c1: "#1a1a2e", c2: "#4a6fa5",
    summary: "一艘过夜渡轮靠岸时少了一名乘客。船舱监控完好，唯独少了午夜十二点那一分钟。"
  },
  {
    id: 4, title: "盛夏轨道", score: 8.5, category: "爱情", duration: 112,
    region: "中国大陆", release: "2026-10-18", director: "唐觅",
    actors: "苏晓 / 何川", hot: true, wish: 0, tag: "热映",
    formats: ["2D", "3D"], c1: "#ff8c6b", c2: "#6b2d5b",
    summary: "末班地铁反复停在同一站。两个互相错过的人，只好在这一站把夏天重新过完。"
  },
  {
    id: 5, title: "山河棋局", score: null, category: "剧情", duration: 136,
    region: "中国大陆", release: "2026-11-06", director: "赵衡",
    actors: "韩砺 / 乔晚", hot: false, wish: 286430, tag: "预售",
    formats: ["2D", "IMAX"], c1: "#2c3e50", c2: "#c0392b",
    summary: "少年在废弃棋社捡到一盘下了一半的棋。每落下一步，城里就有一条老街改变走向。"
  },
  {
    id: 6, title: "星尘旅店", score: null, category: "喜剧", duration: 108,
    region: "中国大陆", release: "2026-11-14", director: "马亮",
    actors: "丁一乐 / 崔果", hot: false, wish: 164220, tag: "预售",
    formats: ["2D"], c1: "#1b3a2f", c2: "#f0c27a",
    summary: "公路边上的旅店只在流星夜营业。客人第二天离开时，都会忘掉自己为什么要住下来。"
  },
  {
    id: 7, title: "雾港证人", score: null, category: "悬疑", duration: 121,
    region: "中国大陆", release: "2026-11-21", director: "沈酌",
    actors: "叶准 / 阮青", hot: false, wish: 98210, tag: "预售",
    formats: ["2D"], c1: "#243447", c2: "#7f8c8d",
    summary: "港口命案的三位证人描述了三种天气。调查员发现，这三个人其实站在同一扇窗前。"
  },
  {
    id: 8, title: "赤色邮差", score: null, category: "动画", duration: 102,
    region: "中国大陆", release: "2026-12-04", director: "周蔓",
    actors: "配音：林夏 / 陈朵", hot: false, wish: 351008, tag: "预售",
    formats: ["2D", "3D"], c1: "#7b2d26", c2: "#e6b35a",
    summary: "小城邮差负责投递一封永远写错地址的信。送信的路上，季节会跟着信封上的邮票一起变化。"
  },
  {
    id: 9, title: "轨道以北", score: null, category: "科幻", duration: 133,
    region: "中国大陆", release: "2026-12-18", director: "高远",
    actors: "秦川 / 孟叙", hot: false, wish: 210445, tag: "预售",
    formats: ["2D", "IMAX"], c1: "#102a43", c2: "#3dccc7",
    summary: "北极列车的终点站不在地图上。乘务员告诉乘客，北面还有一站，但列车从不在那里开门。"
  }
];
