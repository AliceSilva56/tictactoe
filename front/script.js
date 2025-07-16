// frontend/script.js
let nomeX = "Gato 1";
let nomeO = "Gato 2";

let rodadaTotal = 3;
let rodadaAtual = 1;
let jogadorAtual = 'X'; // 'X' = Gato 1, 'O' = Gato 2
let tabuleiro = ['', '', '', '', '', '', '', '', ''];
let scoreX = 0;
let scoreO = 0;
let gameOver = false;

const simbolos = {
  X: ' 𓃠',
  O: '🐈‍⬛'
};

function startGame(rodadas) {
  nomeX = document.getElementById("nomeX").value.trim() || "Gato 1";
  nomeO = document.getElementById("nomeO").value.trim() || "Gato 2";

  rodadaTotal = rodadas;
  rodadaAtual = 1;
  scoreX = 0;
  scoreO = 0;
  document.getElementById("totalRodadas").textContent = rodadas;
  document.getElementById("rodadaAtual").textContent = rodadaAtual;
  document.getElementById("scoreX").textContent = 0;
  document.getElementById("scoreO").textContent = 0;

  document.getElementById("nomeDisplayX").textContent = `𓃠 ${nomeX}`;
  document.getElementById("nomeDisplayO").textContent = `🐈‍⬛ ${nomeO}`;

  document.getElementById("menu").classList.add("hidden");
  document.getElementById("game").classList.remove("hidden");

  resetTabuleiro();
}


function resetTabuleiro() {
  tabuleiro = ['', '', '', '', '', '', '', '', ''];
  gameOver = false;
  jogadorAtual = 'X';
  document.getElementById("mensagem").textContent = '';

  const tabuleiroEl = document.getElementById("tabuleiro");
  tabuleiroEl.innerHTML = '';

  for (let i = 0; i < 9; i++) {
    const casa = document.createElement("div");
    casa.dataset.index = i;
    casa.addEventListener("click", handleClick);
    tabuleiroEl.appendChild(casa);
  }

  document.getElementById("rodadaAtual").textContent = rodadaAtual;
}

function handleClick(event) {
  const index = event.target.dataset.index;

  if (tabuleiro[index] !== '' || gameOver) return;

  tabuleiro[index] = jogadorAtual;
  event.target.textContent = simbolos[jogadorAtual];

  if (verificarVitoria(jogadorAtual)) {
    gameOver = true;
    document.getElementById("mensagem").textContent = `🎉 ${jogadorAtual === 'X' ? nomeX : nomeO} venceu esta rodada!`;


    if (jogadorAtual === 'X') {
      scoreX++;
      document.getElementById("scoreX").textContent = scoreX;
    } else {
      scoreO++;
      document.getElementById("scoreO").textContent = scoreO;
    }

    finalizarRodada();
    return;
  }

  if (!tabuleiro.includes('')) {
    gameOver = true;
    document.getElementById("mensagem").textContent = `😼 Empate!`;
    finalizarRodada();
    return;
  }

  jogadorAtual = jogadorAtual === 'X' ? 'O' : 'X';
}

function verificarVitoria(jogador) {
  const combinacoes = [
    [0,1,2], [3,4,5], [6,7,8], // linhas
    [0,3,6], [1,4,7], [2,5,8], // colunas
    [0,4,8], [2,4,6]           // diagonais
  ];

  return combinacoes.some(comb =>
    comb.every(i => tabuleiro[i] === jogador)
  );
}

function finalizarRodada() {
  setTimeout(() => {
    rodadaAtual++;
    if (rodadaAtual > rodadaTotal) {
      mostrarVencedorFinal();
    } else {
      resetTabuleiro();
    }
  }, 2000);
}

function mostrarVencedorFinal() {
  let msg = '';

  if (scoreX > scoreO) {
  msg = `🏆 ${nomeX} venceu o duelo com ${scoreX} rodadas!`;
} else if (scoreO > scoreX) {
  msg = `🏆 ${nomeO} venceu o duelo com ${scoreO} rodadas!`;
} else {
  msg = `🐾 O duelo terminou em empate total!`;
}


  document.getElementById("mensagem").textContent = msg;

  setTimeout(() => {
    document.getElementById("game").classList.add("hidden");
    document.getElementById("menu").classList.remove("hidden");
  }, 4000);
}
