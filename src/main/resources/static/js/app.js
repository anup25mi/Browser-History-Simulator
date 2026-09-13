/**
 * Browser History Simulator - Client & DSA Engine
 * 
 * College Java DSA Mini-Project Simulator
 * 
 * Features:
 * 1. Hybrid Engine:
 *    - Connects to Spring Boot + MySQL backend if available.
 *    - Automatically falls back to in-browser Custom Two-Stack DSA engine
 *      (Zero setup: no MySQL, no backend, no terminal needed!).
 * 2. Full Two-Stack LIFO Implementation:
 *    - Custom Node
 *    - BackStack (push, pop, peek, isEmpty, size)
 *    - ForwardStack (push, pop, peek, isEmpty, size)
 * 3. Dynamic Webpage Previews (Google, YouTube, GitHub, Wikipedia, etc.)
 * 4. Real-time Stack Visualizer with TOP pointer
 * 5. History Logger with timestamps and Clear History
 */

// ========================================================
// Custom DSA Implementation (Mirroring Java Backend Classes)
// ========================================================

class CustomNode {
  constructor(url) {
    this.url = url;
    this.next = null;
  }
}

class CustomStack {
  constructor(name) {
    this.name = name;
    this.top = null;
    this.count = 0;
  }

  push(url) {
    if (!url || !url.trim()) return;
    const newNode = new CustomNode(url.trim());
    newNode.next = this.top;
    this.top = newNode;
    this.count++;
  }

  pop() {
    if (this.isEmpty()) return null;
    const poppedUrl = this.top.url;
    this.top = this.top.next;
    this.count--;
    return poppedUrl;
  }

  peek() {
    if (this.isEmpty()) return null;
    return this.top.url;
  }

  isEmpty() {
    return this.top === null;
  }

  size() {
    return this.count;
  }

  clear() {
    this.top = null;
    this.count = 0;
  }

  toList() {
    const list = [];
    let current = this.top;
    while (current !== null) {
      list.push(current.url);
      current = current.next;
    }
    return list;
  }
}

// In-Memory Simulation State
const clientBackStack = new CustomStack('Back');
const clientForwardStack = new CustomStack('Forward');
let clientCurrentPage = 'about:blank';
let clientHistory = [];
let clientHistoryId = 1;
let isBackendAvailable = false;

// DOM Elements
const btnBack = document.getElementById('btnBack');
const btnForward = document.getElementById('btnForward');
const btnRefresh = document.getElementById('btnRefresh');
const urlForm = document.getElementById('urlForm');
const urlInput = document.getElementById('urlInput');
const btnClearInput = document.getElementById('btnClearInput');
const viewport = document.getElementById('viewport');
const tabTitle = document.getElementById('tabTitle');
const tabIcon = document.getElementById('tabIcon');
const statusText = document.getElementById('statusText');
const statusCurrentUrl = document.getElementById('statusCurrentUrl');

// DSA Visualizer Elements
const backStackVisual = document.getElementById('backStackVisual');
const forwardStackVisual = document.getElementById('forwardStackVisual');
const backStackSize = document.getElementById('backStackSize');
const forwardStackSize = document.getElementById('forwardStackSize');
const currentNodeDisplay = document.getElementById('currentNodeDisplay');

// History Table Elements
const historyTableBody = document.getElementById('historyTableBody');
const historyCountBadge = document.getElementById('historyCountBadge');
const btnClearHistory = document.getElementById('btnClearHistory');

// Tab switcher
const tabDsaBtn = document.getElementById('tabDsaBtn');
const tabHistoryBtn = document.getElementById('tabHistoryBtn');
const dsaPanel = document.getElementById('dsaPanel');
const historyPanel = document.getElementById('historyPanel');

// Base API URL
const API_BASE = '/api/browser';

// ========================================================
// 1. Initialization
// ========================================================
document.addEventListener('DOMContentLoaded', async () => {
  setupEventListeners();
  loadSavedClientHistory();
  await checkBackendAndInitialize();
});

function setupEventListeners() {
  // Navigation Form Submit (Visit URL)
  urlForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const url = urlInput.value.trim();
    if (!url) {
      showToast('Please enter a website URL to visit.', 'error');
      urlInput.focus();
      return;
    }
    visitPage(url);
  });

  // Back Button
  btnBack.addEventListener('click', () => {
    navigateBack();
  });

  // Forward Button
  btnForward.addEventListener('click', () => {
    navigateForward();
  });

  // Refresh Button
  btnRefresh.addEventListener('click', () => {
    const current = statusCurrentUrl.textContent;
    if (current && current !== 'about:blank') {
      showToast(`Refreshing ${current}...`, 'info');
      renderSimulatedPage(current);
    } else {
      updateBrowserUI(getCurrentClientState());
    }
  });

  // Clear Input 'x' button
  urlInput.addEventListener('input', () => {
    btnClearInput.style.display = urlInput.value ? 'block' : 'none';
  });

  btnClearInput.addEventListener('click', () => {
    urlInput.value = '';
    btnClearInput.style.display = 'none';
    urlInput.focus();
  });

  // Quick Visit Bookmark Pills
  document.querySelectorAll('.pill').forEach(pill => {
    pill.addEventListener('click', () => {
      const targetUrl = pill.getAttribute('data-url');
      urlInput.value = targetUrl;
      btnClearInput.style.display = 'block';
      visitPage(targetUrl);
    });
  });

  // Clear History Button
  btnClearHistory.addEventListener('click', () => {
    if (confirm('Are you sure you want to clear all browsing history and reset session stacks?')) {
      clearAllHistory();
    }
  });

  // Sidebar Tab Switcher
  tabDsaBtn.addEventListener('click', () => {
    tabDsaBtn.classList.add('active');
    tabHistoryBtn.classList.remove('active');
    dsaPanel.classList.add('active');
    historyPanel.classList.remove('active');
  });

  tabHistoryBtn.addEventListener('click', () => {
    tabHistoryBtn.classList.add('active');
    tabDsaBtn.classList.remove('active');
    historyPanel.classList.add('active');
    dsaPanel.classList.remove('active');
  });
}

// ========================================================
// 2. Hybrid Backend / Standalone Controller
// ========================================================

async function checkBackendAndInitialize() {
  if (window.location.protocol === 'file:') {
    isBackendAvailable = false;
    initStandaloneMode('Local Standalone Mode (Zero Setup)');
    return;
  }

  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 1200);
    const res = await fetch(`${API_BASE}/state`, { signal: controller.signal });
    clearTimeout(timeoutId);
    
    if (res.ok) {
      const result = await res.json();
      if (result.success) {
        isBackendAvailable = true;
        updateBrowserUI(result.data);
        loadBackendHistory();
        showToast('Connected to Spring Boot & MySQL backend!', 'success');
        return;
      }
    }
  } catch (err) {
    // Backend not running
  }

  isBackendAvailable = false;
  initStandaloneMode('Standalone DSA Engine (Ready & Active)');
}

function initStandaloneMode(statusMsg) {
  setStatus(statusMsg);
  updateBrowserUI(getCurrentClientState());
  renderHistoryTable(clientHistory);
}

function getCurrentClientState() {
  return {
    currentPage: clientCurrentPage,
    canGoBack: !clientBackStack.isEmpty(),
    canGoForward: !clientForwardStack.isEmpty(),
    backStackSize: clientBackStack.size(),
    forwardStackSize: clientForwardStack.size(),
    backStack: clientBackStack.toList(),
    forwardStack: clientForwardStack.toList(),
    totalHistoryCount: clientHistory.length
  };
}

function normalizeUrl(url) {
  if (!url) return '';
  let trimmed = url.trim();
  if (trimmed.toLowerCase() === 'about:blank') return 'about:blank';
  if (!trimmed.startsWith('http://') && !trimmed.startsWith('https://')) {
    return 'https://' + trimmed;
  }
  return trimmed;
}

// ========================================================
// 3. Navigation Actions
// ========================================================

async function visitPage(rawUrl) {
  const url = normalizeUrl(rawUrl);

  if (isBackendAvailable) {
    try {
      setStatus('Visiting page...');
      const res = await fetch(`${API_BASE}/visit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ url })
      });
      const result = await res.json();
      if (result.success) {
        updateBrowserUI(result.data);
        loadBackendHistory();
        showToast(`Loaded: ${result.data.currentPage}`, 'success');
        return;
      }
    } catch (e) {
      // fallback
    }
  }

  // --- Standalone Two-Stack Algorithm ---
  // 1. Push current page to Back Stack (if valid and not about:blank)
  if (clientCurrentPage && clientCurrentPage !== 'about:blank') {
    clientBackStack.push(clientCurrentPage);
  }

  // 2. Set new URL as current page
  clientCurrentPage = url;

  // 3. Clear Forward Stack (visiting new branch destroys forward history!)
  clientForwardStack.clear();

  // 4. Save to History
  const now = new Date();
  clientHistory.unshift({
    id: clientHistoryId++,
    url: url,
    visitedAt: now.toISOString(),
    formattedVisitedAt: now.toLocaleString()
  });
  saveClientHistory();

  // 5. Update UI
  const state = getCurrentClientState();
  updateBrowserUI(state);
  renderHistoryTable(clientHistory);
  showToast(`Loaded: ${url}`, 'success');
  setStatus('Ready');
}

async function navigateBack() {
  if (isBackendAvailable) {
    try {
      setStatus('Navigating back...');
      const res = await fetch(`${API_BASE}/back`, { method: 'POST' });
      const result = await res.json();
      if (result.success) {
        updateBrowserUI(result.data);
        showToast(`Navigated back to ${result.data.currentPage}`, 'info');
        return;
      }
    } catch (e) {
      // fallback
    }
  }

  // --- Standalone Back Algorithm ---
  if (clientBackStack.isEmpty()) {
    showToast('Cannot go back: Back Stack is empty!', 'error');
    return;
  }

  // Push current page onto Forward Stack
  clientForwardStack.push(clientCurrentPage);

  // Pop previous URL from Back Stack & make it current
  clientCurrentPage = clientBackStack.pop();

  const state = getCurrentClientState();
  updateBrowserUI(state);
  showToast(`Navigated back to ${clientCurrentPage}`, 'info');
  setStatus('Ready');
}

async function navigateForward() {
  if (isBackendAvailable) {
    try {
      setStatus('Navigating forward...');
      const res = await fetch(`${API_BASE}/forward`, { method: 'POST' });
      const result = await res.json();
      if (result.success) {
        updateBrowserUI(result.data);
        showToast(`Navigated forward to ${result.data.currentPage}`, 'info');
        return;
      }
    } catch (e) {
      // fallback
    }
  }

  // --- Standalone Forward Algorithm ---
  if (clientForwardStack.isEmpty()) {
    showToast('Cannot go forward: Forward Stack is empty!', 'error');
    return;
  }

  // Push current page onto Back Stack
  clientBackStack.push(clientCurrentPage);

  // Pop next URL from Forward Stack & make it current
  clientCurrentPage = clientForwardStack.pop();

  const state = getCurrentClientState();
  updateBrowserUI(state);
  showToast(`Navigated forward to ${clientCurrentPage}`, 'info');
  setStatus('Ready');
}

async function clearAllHistory() {
  if (isBackendAvailable) {
    try {
      const res = await fetch(`${API_BASE}/history`, { method: 'DELETE' });
      const result = await res.json();
      if (result.success) {
        updateBrowserUI(result.data);
        loadBackendHistory();
        showToast('All browsing history deleted.', 'success');
        return;
      }
    } catch (e) {
      // fallback
    }
  }

  // Standalone Clear
  clientBackStack.clear();
  clientForwardStack.clear();
  clientCurrentPage = 'about:blank';
  clientHistory = [];
  saveClientHistory();

  const state = getCurrentClientState();
  updateBrowserUI(state);
  renderHistoryTable([]);
  showToast('All history and session stacks cleared.', 'success');
}

async function loadBackendHistory() {
  try {
    const res = await fetch(`${API_BASE}/history`);
    const result = await res.json();
    if (result.success) {
      renderHistoryTable(result.data);
    }
  } catch (err) {
    console.error('History load error:', err);
  }
}

// Local storage helpers
function saveClientHistory() {
  try {
    localStorage.setItem('browser_simulator_history', JSON.stringify(clientHistory));
  } catch (e) {}
}

function loadSavedClientHistory() {
  try {
    const saved = localStorage.getItem('browser_simulator_history');
    if (saved) {
      clientHistory = JSON.parse(saved);
      if (clientHistory.length > 0) {
        clientHistoryId = clientHistory[0].id + 1;
      }
    }
  } catch (e) {}
}

// ========================================================
// 4. UI Rendering
// ========================================================

function updateBrowserUI(state) {
  if (!state) return;

  const currentUrl = state.currentPage || 'about:blank';

  // 1. Navigation buttons state
  btnBack.disabled = !state.canGoBack;
  btnForward.disabled = !state.canGoForward;

  // 2. Address bar
  if (currentUrl !== 'about:blank') {
    urlInput.value = currentUrl;
    btnClearInput.style.display = 'block';
  } else {
    urlInput.value = '';
    btnClearInput.style.display = 'none';
  }

  // 3. Status bar
  statusCurrentUrl.textContent = currentUrl;

  // 4. Viewport & Tab
  updateTabAndViewport(currentUrl);

  // 5. Two-Stack Visualizer
  renderStacks(state);

  // 6. History Badge
  historyCountBadge.textContent = state.totalHistoryCount || 0;

  // 7. Highlight active history row
  highlightActiveHistoryRow(currentUrl);
}

function updateTabAndViewport(url) {
  if (!url || url === 'about:blank') {
    tabTitle.textContent = 'Home Page';
    tabIcon.textContent = '🌐';
    viewport.innerHTML = `
      <div class="viewport-placeholder">
        <div class="welcome-card">
          <h2>Welcome to Browser History Simulator!</h2>
          <p>Type a website address above or click any of the Quick Visit buttons to start browsing.</p>
          <div class="welcome-features">
            <div class="feature-item">
              <span class="icon">⏮️</span>
              <div>
                <strong>Back Button</strong>
                <p>Pops previous page from <code>BackStack</code> into <code>ForwardStack</code>.</p>
              </div>
            </div>
            <div class="feature-item">
              <span class="icon">⏭️</span>
              <div>
                <strong>Forward Button</strong>
                <p>Pops next page from <code>ForwardStack</code> into <code>BackStack</code>.</p>
              </div>
            </div>
            <div class="feature-item">
              <span class="icon">🥞</span>
              <div>
                <strong>Custom Two-Stack DSA</strong>
                <p>Full LIFO Linked-List Stacks running in $\\mathcal{O}(1)$ time with live visualization.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    `;
    return;
  }

  renderSimulatedPage(url);
}

function renderSimulatedPage(url) {
  const parsed = getDomainDetails(url);

  tabTitle.textContent = parsed.title;
  tabIcon.textContent = parsed.icon;

  let pageContentHtml = '';

  if (parsed.domain.includes('google')) {
    pageContentHtml = `
      <div class="simulated-page google-mock">
        <div class="google-logo">
          <span>G</span><span>o</span><span>o</span><span>g</span><span>l</span><span>e</span>
        </div>
        <div class="google-search-box">
          <span>🔍</span>
          <input type="text" placeholder="Search Google or type a URL" value="Java DSA Browser History Simulator" readonly />
          <span>🎤</span>
        </div>
        <div class="google-buttons">
          <button class="google-btn">Google Search</button>
          <button class="google-btn">I'm Feeling Lucky</button>
        </div>
      </div>
    `;
  } else if (parsed.domain.includes('youtube')) {
    pageContentHtml = `
      <div class="simulated-page youtube-mock">
        <div class="yt-banner">
          <span>▶️</span> YouTube Video Stream
        </div>
        <div class="yt-grid">
          <div class="yt-card">
            <div class="yt-thumb">📹</div>
            <div class="yt-info">
              <h4>Java Stacks & Queues Explained</h4>
              <p>College DSA Tutorial &bull; 100K views</p>
            </div>
          </div>
          <div class="yt-card">
            <div class="yt-thumb">💻</div>
            <div class="yt-info">
              <h4>Spring Boot 3 + MySQL Full Course</h4>
              <p>FreeCodeCamp &bull; 450K views</p>
            </div>
          </div>
          <div class="yt-card">
            <div class="yt-thumb">🚀</div>
            <div class="yt-info">
              <h4>How Browser History Works (DSA)</h4>
              <p>Tech Prep &bull; 89K views</p>
            </div>
          </div>
        </div>
      </div>
    `;
  } else if (parsed.domain.includes('github')) {
    pageContentHtml = `
      <div class="simulated-page github-mock">
        <div class="gh-repo-header">
          <span>📂</span>
          <strong>student / browser-history-simulator</strong>
          <span class="gh-badge">Public</span>
        </div>
        <div class="gh-files-box">
          <div class="gh-file-row"><span>📁</span> src/main/java/com/example/browserhistory/dsa/BackStack.java</div>
          <div class="gh-file-row"><span>📁</span> src/main/java/com/example/browserhistory/dsa/ForwardStack.java</div>
          <div class="gh-file-row"><span>📁</span> src/main/java/com/example/browserhistory/service/BrowserNavigationService.java</div>
          <div class="gh-file-row"><span>📄</span> pom.xml</div>
          <div class="gh-file-row"><span>📄</span> README.md</div>
        </div>
      </div>
    `;
  } else {
    pageContentHtml = `
      <div class="simulated-page">
        <div class="generic-mock-card">
          <div class="sim-header">
            <div class="sim-domain-tag">
              <span class="sim-favicon">${parsed.icon}</span>
              <span class="sim-domain-name">${parsed.domain}</span>
            </div>
            <span class="sim-badge-200">HTTP 200 OK</span>
          </div>
          <h3>${parsed.title}</h3>
          <p style="color: #475569; margin-top: 8px;">
            Simulated website content loaded successfully. This page is currently stored as the active node in the browser navigation state.
          </p>
          <div class="generic-meta-info">
            <div><strong>Full URL:</strong> ${url}</div>
            <div><strong>Protocol:</strong> HTTPS (Encrypted Local Simulation)</div>
            <div><strong>Status:</strong> Active Page in Simulator</div>
          </div>
        </div>
      </div>
    `;
  }

  viewport.innerHTML = pageContentHtml;
}

function getDomainDetails(url) {
  let domain = url.replace(/^(https?:\/\/)?(www\.)?/, '').split('/')[0];
  let title = domain;
  let icon = '🌐';

  if (domain.includes('google')) {
    title = 'Google Search';
    icon = '🔍';
  } else if (domain.includes('youtube')) {
    title = 'YouTube - Watch Videos';
    icon = '▶️';
  } else if (domain.includes('github')) {
    title = 'GitHub - Code Repository';
    icon = '🐙';
  } else if (domain.includes('wikipedia')) {
    title = 'Wikipedia - Free Encyclopedia';
    icon = '📚';
  } else if (domain.includes('stackoverflow')) {
    title = 'Stack Overflow - Developer Q&A';
    icon = '🥞';
  }

  return { domain, title, icon };
}

function renderStacks(state) {
  backStackSize.textContent = state.backStackSize;
  renderStackVisual(backStackVisual, state.backStack, 'Back');

  currentNodeDisplay.innerHTML = `
    <span class="current-node-url">${state.currentPage || 'about:blank'}</span>
  `;

  forwardStackSize.textContent = state.forwardStackSize;
  renderStackVisual(forwardStackVisual, state.forwardStack, 'Forward');
}

function renderStackVisual(container, items, stackType) {
  if (!items || items.length === 0) {
    container.innerHTML = `<div class="stack-empty">${stackType} Stack is empty</div>`;
    return;
  }

  let html = '';
  items.forEach((url, index) => {
    const isTop = index === 0;
    html += `
      <div class="stack-item ${isTop ? 'is-top' : ''}">
        <span title="${url}">${truncate(url, 26)}</span>
        ${isTop ? '<span class="top-tag">TOP</span>' : ''}
      </div>
    `;
  });
  container.innerHTML = html;
}

function renderHistoryTable(items) {
  if (!items || items.length === 0) {
    historyTableBody.innerHTML = `
      <tr>
        <td colspan="3" class="table-empty">No browsing history yet. Visit a webpage to record history!</td>
      </tr>
    `;
    historyCountBadge.textContent = '0';
    return;
  }

  historyCountBadge.textContent = items.length;
  const currentUrl = statusCurrentUrl.textContent;

  let html = '';
  items.forEach(item => {
    const isActive = item.url === currentUrl;
    html += `
      <tr class="${isActive ? 'is-active-row' : ''}">
        <td>#${item.id}</td>
        <td title="${item.url}">
          <a href="javascript:void(0)" onclick="quickVisit('${item.url}')" style="color: inherit; text-decoration: none;">
            ${truncate(item.url, 45)}
            ${isActive ? ' <span style="font-size: 0.7rem; color: #60a5fa;">(Current)</span>' : ''}
          </a>
        </td>
        <td style="color: #94a3b8;">${item.formattedVisitedAt || formatDateTime(item.visitedAt)}</td>
      </tr>
    `;
  });

  historyTableBody.innerHTML = html;
}

function highlightActiveHistoryRow(currentUrl) {
  document.querySelectorAll('#historyTableBody tr').forEach(row => {
    row.classList.remove('is-active-row');
  });
}

window.quickVisit = function(url) {
  urlInput.value = url;
  btnClearInput.style.display = 'block';
  visitPage(url);
};

function setStatus(text) {
  statusText.textContent = text;
}

function truncate(str, maxLen) {
  if (!str) return '';
  return str.length > maxLen ? str.substring(0, maxLen - 3) + '...' : str;
}

function formatDateTime(isoString) {
  if (!isoString) return '';
  try {
    const d = new Date(isoString);
    return d.toLocaleString();
  } catch (e) {
    return isoString;
  }
}

function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;

  let icon = 'ℹ️';
  if (type === 'success') icon = '✅';
  if (type === 'error') icon = '⚠️';

  toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(50px)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3000);
}
