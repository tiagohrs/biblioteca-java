// =====================================================
// CONFIGURAÇÃO: endereço da sua API Spring Boot
// =====================================================
const API_URL = "http://localhost:9090";

// Cada "recurso" descreve uma tela (livros ou leitores)
const RECURSOS = {
  livros: {
    rota: "/livros",
    titulo: "Livros",
    novo: "Novo livro",
    editar: "Editar livro",
    vazio: "Nenhum livro cadastrado. Clique em “Novo livro” para começar.",
    proporcao: "capa",
    imagem: { campo: "nomeArquivoCapa", pasta: "capas", rota: "capa", rotulo: "capa" },
    campos: [
      { nome: "titulo", rotulo: "Título", tipo: "text" },
      { nome: "autor", rotulo: "Autor", tipo: "text" },
      { nome: "anoPublicacao", rotulo: "Ano de publicação", tipo: "number" },
    ],
    nomeDoItem: (i) => i.titulo,
    detalheDoItem: (i) => `${i.autor}, ${i.anoPublicacao}`,
  },
  leitores: {
    rota: "/leitores",
    titulo: "Leitores",
    novo: "Novo leitor",
    editar: "Editar leitor",
    vazio: "Nenhum leitor cadastrado. Clique em “Novo leitor” para começar.",
    proporcao: "foto",
    imagem: { campo: "nomeArquivoFoto", pasta: "fotos-alunos", rota: "foto", rotulo: "foto" },
    campos: [
      { nome: "nome", rotulo: "Nome", tipo: "text" },
      { nome: "email", rotulo: "E-mail", tipo: "email" },
    ],
    nomeDoItem: (i) => i.nome,
    detalheDoItem: (i) => i.email,
  },
};

const CORES_DA_CAPA = ["#2f5d62", "#7a3e48", "#3e4c7a", "#6b5b2e", "#4a3f6b", "#2e6b4f"];

const estado = { aba: "livros", itens: [], editando: null, modoLogin: "entrar" };
let urlsDeImagem = [];
let temporizadorAviso;

// ---------- Atalhos ----------
const $ = (id) => document.getElementById(id);

function criar(tag, classe) {
  const e = document.createElement(tag);
  if (classe) e.className = classe;
  return e;
}

function aviso(texto, erro = false) {
  const caixa = $("aviso");
  caixa.textContent = texto;
  caixa.className = erro ? "erro" : "";
  caixa.hidden = false;
  clearTimeout(temporizadorAviso);
  temporizadorAviso = setTimeout(() => (caixa.hidden = true), 5000);
}

// ---------- Comunicação com a API ----------
async function lerErro(resposta) {
  try {
    const dados = await resposta.json();
    if (dados.message) return dados.message;
    if (dados.erro) return dados.erro;
    if (dados.mensagem) return dados.mensagem;
    if (typeof dados === "object") return Object.values(dados).join(". ");
  } catch (e) {
    // resposta sem JSON
  }
  return `Erro ${resposta.status} ao falar com a API.`;
}

async function api(caminho, opcoes = {}) {
  const { semToken, ...configFetch } = opcoes;
  const headers = { ...(configFetch.headers || {}) };
  const token = localStorage.getItem("token");
  if (token && !semToken) headers.Authorization = "Bearer " + token;

  let resposta;
  try {
    resposta = await fetch(API_URL + caminho, { ...configFetch, headers });
  } catch (e) {
    throw new Error(
      "Não consegui falar com a API em " + API_URL +
      ". Confira se o Spring está rodando. Se estiver, pode ser bloqueio de CORS (veja o Console com F12)."
    );
  }

  if (!semToken && (resposta.status === 401 || resposta.status === 403)) {
    sair("Sua sessão expirou. Entre de novo.");
    throw new Error("Sessão expirada.");
  }
  if (!resposta.ok) throw new Error(await lerErro(resposta));
  if (resposta.status === 204) return null;

  const texto = await resposta.text();
  return texto ? JSON.parse(texto) : null;
}

// ---------- Login ----------
function mostrarLogin() {
  $("tela-app").hidden = true;
  $("tela-login").hidden = false;
}

function mostrarApp() {
  $("tela-login").hidden = true;
  $("tela-app").hidden = false;
  carregar();
}

function sair(mensagem) {
  localStorage.removeItem("token");
  mostrarLogin();
  if (mensagem) aviso(mensagem, true);
}

function alternarModoLogin() {
  estado.modoLogin = estado.modoLogin === "entrar" ? "criar" : "entrar";
  const criando = estado.modoLogin === "criar";
  $("login-subtitulo").textContent = criando
    ? "Crie um usuário para acessar o sistema."
    : "Entre para gerenciar livros e leitores.";
  $("login-enviar").textContent = criando ? "Criar usuário e entrar" : "Entrar";
  $("login-alternar").textContent = criando ? "Já tenho usuário" : "Ainda não tenho usuário";
}

async function enviarLogin(evento) {
  evento.preventDefault();
  const botao = $("login-enviar");
  const corpo = JSON.stringify({
    login: $("login-usuario").value.trim(),
    senha: $("login-senha").value,
  });
  const cabecalho = { "Content-Type": "application/json" };

  botao.disabled = true;
  try {
    if (estado.modoLogin === "criar") {
      await api("/usuarios", { method: "POST", headers: cabecalho, body: corpo, semToken: true });
    }
    const dados = await api("/auth/login", { method: "POST", headers: cabecalho, body: corpo, semToken: true });
    localStorage.setItem("token", dados.token);
    $("form-login").reset();
    mostrarApp();
  } catch (erro) {
    aviso(erro.message, true);
  } finally {
    botao.disabled = false;
  }
}

// ---------- Lista ----------
async function carregar() {
  const cfg = RECURSOS[estado.aba];
  $("titulo-lista").textContent = cfg.titulo;
  $("btn-novo").textContent = cfg.novo;
  try {
    estado.itens = await api(cfg.rota);
    desenharLista();
  } catch (erro) {
    if (erro.message !== "Sessão expirada.") aviso(erro.message, true);
  }
}

function iniciais(nome) {
  return nome
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0].toUpperCase())
    .join("");
}

async function carregarImagem(img, reserva, caminho) {
  try {
    const resposta = await fetch(API_URL + caminho, {
      headers: { Authorization: "Bearer " + localStorage.getItem("token") },
    });
    if (!resposta.ok) throw new Error("sem imagem");
    const url = URL.createObjectURL(await resposta.blob());
    urlsDeImagem.push(url);
    img.src = url;
    img.hidden = false;
    reserva.hidden = true;
  } catch (e) {
    // mantém o quadro colorido no lugar da imagem
  }
}

function criarCartao(item) {
  const cfg = RECURSOS[estado.aba];
  const nome = cfg.nomeDoItem(item);
  const nomeArquivo = item[cfg.imagem.campo];

  const cartao = criar("article", "card");

  // imagem (ou quadro colorido enquanto não há imagem)
  const moldura = criar("div", "moldura " + cfg.proporcao);
  const reserva = criar("div", "reserva");
  reserva.style.background = CORES_DA_CAPA[item.id % CORES_DA_CAPA.length];
  const textoReserva = criar("span", "reserva-texto");
  textoReserva.textContent = estado.aba === "livros" ? nome : iniciais(nome);
  reserva.append(textoReserva);
  const img = criar("img");
  img.alt = `${cfg.imagem.rotulo} de ${nome}`;
  img.hidden = true;
  moldura.append(reserva, img);
  if (nomeArquivo) {
    carregarImagem(img, reserva, `/uploads/${cfg.imagem.pasta}/${encodeURIComponent(nomeArquivo)}`);
  }

  // textos
  const info = criar("div", "info");
  const titulo = criar("h3");
  titulo.textContent = nome;
  const detalhe = criar("p");
  detalhe.textContent = cfg.detalheDoItem(item);
  info.append(titulo, detalhe);

  // botões
  const acoes = criar("div", "acoes");

  const entrada = criar("input");
  entrada.type = "file";
  entrada.accept = "image/*";
  entrada.hidden = true;
  entrada.addEventListener("change", () => {
    if (entrada.files[0]) enviarImagem(item.id, entrada.files[0]);
  });

  const botaoImagem = criar("button", "botao secundario pequeno");
  botaoImagem.type = "button";
  botaoImagem.textContent = (nomeArquivo ? "Trocar " : "Enviar ") + cfg.imagem.rotulo;
  botaoImagem.addEventListener("click", () => entrada.click());

  const botaoEditar = criar("button", "botao secundario pequeno");
  botaoEditar.type = "button";
  botaoEditar.textContent = "Editar";
  botaoEditar.addEventListener("click", () => abrirFormulario(item));

  const botaoExcluir = criar("button", "botao perigo pequeno");
  botaoExcluir.type = "button";
  botaoExcluir.textContent = "Excluir";
  botaoExcluir.addEventListener("click", () => excluir(item));

  acoes.append(entrada, botaoImagem, botaoEditar, botaoExcluir);
  cartao.append(moldura, info, acoes);
  return cartao;
}

function desenharLista() {
  urlsDeImagem.forEach((u) => URL.revokeObjectURL(u));
  urlsDeImagem = [];

  const lista = $("lista");
  lista.replaceChildren(...estado.itens.map(criarCartao));

  const vazio = $("vazio");
  vazio.hidden = estado.itens.length > 0;
  vazio.textContent = RECURSOS[estado.aba].vazio;
}

// ---------- Imagens ----------
async function enviarImagemPara(id, arquivo) {
  const cfg = RECURSOS[estado.aba];
  const dados = new FormData();
  dados.append("arquivo", arquivo); // o nome "arquivo" precisa ser igual ao do @RequestParam
  // Não defina Content-Type aqui: o navegador coloca sozinho.
  await api(`${cfg.rota}/${id}/${cfg.imagem.rota}`, { method: "POST", body: dados });
}

async function enviarImagem(id, arquivo) {
  try {
    await enviarImagemPara(id, arquivo);
    aviso("Imagem enviada.");
    await carregar();
  } catch (erro) {
    if (erro.message !== "Sessão expirada.") aviso(erro.message, true);
  }
}

// ---------- Cadastro, edição e exclusão ----------
function abrirFormulario(item = null) {
  const cfg = RECURSOS[estado.aba];
  estado.editando = item;

  $("modal-titulo").textContent = item ? cfg.editar : cfg.novo;
  $("rotulo-imagem").textContent = (item ? "Trocar " : "Enviar ") + cfg.imagem.rotulo + " (opcional)";
  $("campo-imagem").value = "";

  const area = $("campos");
  area.replaceChildren();
  for (const campo of cfg.campos) {
    const rotulo = criar("label");
    rotulo.htmlFor = "campo-" + campo.nome;
    rotulo.textContent = campo.rotulo;
    const entrada = criar("input");
    entrada.id = "campo-" + campo.nome;
    entrada.name = campo.nome;
    entrada.type = campo.tipo;
    entrada.required = true;
    entrada.value = item ? item[campo.nome] ?? "" : "";
    area.append(rotulo, entrada);
  }
  $("modal").showModal();
}

async function salvar(evento) {
  evento.preventDefault();
  const cfg = RECURSOS[estado.aba];
  const formulario = new FormData($("form-item"));

  const corpo = {};
  for (const campo of cfg.campos) {
    const valor = String(formulario.get(campo.nome)).trim();
    corpo[campo.nome] = campo.tipo === "number" ? Number(valor) : valor;
  }

  const editando = estado.editando;
  const arquivo = $("campo-imagem").files[0];

  try {
    const salvo = await api(editando ? `${cfg.rota}/${editando.id}` : cfg.rota, {
      method: editando ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(corpo),
    });
    if (arquivo) await enviarImagemPara(editando ? editando.id : salvo.id, arquivo);

    $("modal").close();
    aviso(editando ? "Alterações salvas." : "Cadastro feito.");
    await carregar();
  } catch (erro) {
    if (erro.message !== "Sessão expirada.") aviso(erro.message, true);
  }
}

async function excluir(item) {
  const cfg = RECURSOS[estado.aba];
  if (!confirm(`Excluir “${cfg.nomeDoItem(item)}”? Essa ação não pode ser desfeita.`)) return;
  try {
    await api(`${cfg.rota}/${item.id}`, { method: "DELETE" });
    aviso("Excluído.");
    await carregar();
  } catch (erro) {
    if (erro.message !== "Sessão expirada.") aviso(erro.message, true);
  }
}

// ---------- Abas ----------
function trocarAba(aba) {
  estado.aba = aba;
  document.querySelectorAll(".aba").forEach((b) =>
    b.setAttribute("aria-selected", String(b.dataset.aba === aba))
  );
  carregar();
}

// ---------- Início ----------
$("form-login").addEventListener("submit", enviarLogin);
$("login-alternar").addEventListener("click", alternarModoLogin);
$("btn-sair").addEventListener("click", () => sair());
$("btn-novo").addEventListener("click", () => abrirFormulario());
$("btn-cancelar").addEventListener("click", () => $("modal").close());
$("form-item").addEventListener("submit", salvar);
document.querySelectorAll(".aba").forEach((b) =>
  b.addEventListener("click", () => trocarAba(b.dataset.aba))
);

if (localStorage.getItem("token")) mostrarApp();
else mostrarLogin();
