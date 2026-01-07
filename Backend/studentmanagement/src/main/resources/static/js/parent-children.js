// parent-children.js - robust version: detects many id fields, logs response details,
// sends credentials and Authorization if available, and avoids silent mock fallbacks.

(() => {
  const API_BASE_URL = 'http://localhost:8080/api/v1';

  function safeShowMessage(title, message, type = 'info') {
    if (typeof window.showMessage === 'function') {
      try { window.showMessage(title, message, type); return; } catch (e) { /* ignore */ }
    }
    console[type === 'error' ? 'error' : 'log'](`[${title}] ${message}`);
  }

  function getAuthToken() {
    return localStorage.getItem('authToken');
  }

  function getCurrentUserRaw() {
    const raw = localStorage.getItem('currentUser');
    if (!raw) return null;
    try { return JSON.parse(raw); } catch (e) { return null; }
  }

  // return a normalized parent id from many possible shapes
  function resolveParentId(user) {
    if (!user) return null;
    // common fields: id, studentId, student_id, userId, username (email)
    return user.id || user.studentId || user.student_id || user.userId || user.username || user.email || null;
  }

  function buildAuthHeaders() {
    const headers = { 'Content-Type': 'application/json', 'Accept': 'application/json' };
    const token = getAuthToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;
    return headers;
  }

  async function fetchLinkedChildren(parentId) {
    console.debug('fetchLinkedChildren: parentId=', parentId);
    if (!parentId) {
      console.warn('fetchLinkedChildren called with empty parentId');
      return [];
    }
    const headers = buildAuthHeaders();
    try {
      const resp = await fetch(`${API_BASE_URL}/parents/${encodeURIComponent(parentId)}/children`, {
        method: 'GET',
        headers,
        credentials: 'include'
      });

      if (!resp.ok) {
        const text = await resp.text().catch(() => '');
        console.warn('fetchLinkedChildren failed', resp.status, resp.statusText, text);
        // bubble the error up so caller can decide whether to fallback
        const err = new Error(`HTTP ${resp.status} ${resp.statusText}`);
        err.status = resp.status;
        err.body = text;
        throw err;
      }

      const data = await resp.json().catch(() => null);
      if (!Array.isArray(data)) {
        console.warn('fetchLinkedChildren: server returned non-array payload', data);
        return [];
      }
      console.debug('fetchLinkedChildren: got', data.length, 'children');
      return data;
    } catch (err) {
      console.error('fetchLinkedChildren error', err);
      throw err;
    }
  }

  async function fetchFeesForChild(childId) {
    const headers = buildAuthHeaders();
    return fetch(`${API_BASE_URL}/fees/student/${encodeURIComponent(childId)}`, {
      method: 'GET',
      headers,
      credentials: 'include'
    });
  }

  async function populateParentFinancialOverview(childId) {
    const statBalanceEl = document.getElementById('parent-current-balance-display');
    const statDueDateEl = document.getElementById('fee-due-date');
    const detailBalanceEl = document.getElementById('parent-current-balance');
    const lastPaymentEl = document.getElementById('parent-last-payment');
    const trackingLineEl = document.getElementById('student-tracking-line');

    if (!statBalanceEl || !detailBalanceEl || !lastPaymentEl || !trackingLineEl) {
      console.warn('populateParentFinancialOverview: required DOM elements not found; aborting update');
      return;
    }

    [statBalanceEl, detailBalanceEl, lastPaymentEl].forEach(el => { if (el) el.innerText = 'Loading...'; });
    statDueDateEl && (statDueDateEl.innerText = 'Checking...');
    trackingLineEl && (trackingLineEl.textContent = `Tracking performance for: ID ${childId}...`);

    if (!childId) {
      [statBalanceEl, detailBalanceEl, lastPaymentEl].forEach(el => { if (el) el.innerText = 'N/A'; });
      statDueDateEl && (statDueDateEl.innerText = 'N/A');
      trackingLineEl && (trackingLineEl.textContent = `Tracking performance for: No child selected`);
      return;
    }

    try {
      const resp = await fetchFeesForChild(childId);
      if (!resp.ok) {
        const txt = await resp.text().catch(()=>'');
        console.warn('Fees fetch failed', resp.status, txt);
        if (resp.status === 404) {
          [statBalanceEl, detailBalanceEl].forEach(el => { if (el) el.innerText = '$0.00'; });
          lastPaymentEl && (lastPaymentEl.innerText = 'No payments yet');
          statDueDateEl && (statDueDateEl.innerText = 'No dues');
          trackingLineEl && (trackingLineEl.textContent = `Tracking performance for: ID ${childId} (No fee data)`);
          return;
        }
        throw new Error(`HTTP ${resp.status}: ${txt}`);
      }

      const fees = await resp.json().catch(() => []);
      const totalPending = (fees || []).filter(f => f.status !== 'Paid').reduce((s,f)=>s+parseFloat(f.amount||0),0);
      const paid = (fees || []).filter(f => f.status === 'Paid');
      const lastPayment = paid.length ? paid.sort((a,b)=>new Date(b.dueDate)-new Date(a.dueDate))[0] : null;
      const pending = (fees || []).filter(f => f.status !== 'Paid');
      const nextDue = pending.length ? pending.sort((a,b)=>new Date(a.dueDate)-new Date(b.dueDate))[0] : null;

      const currencyFormatter = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', minimumFractionDigits: 2 });
      const dateFormatter = new Intl.DateTimeFormat('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
      const formattedBalance = currencyFormatter.format(totalPending).replace('₹', '$');

      statBalanceEl.innerText = formattedBalance;
      detailBalanceEl.innerText = formattedBalance;
      lastPaymentEl.innerText = lastPayment ? dateFormatter.format(new Date(lastPayment.dueDate)) : 'No payments yet';
      statDueDateEl && (statDueDateEl.innerText = nextDue ? `Due Date: ${dateFormatter.format(new Date(nextDue.dueDate))}` : (totalPending > 0 ? 'Due Date: N/A' : 'No dues'));
      trackingLineEl.innerText = `Tracking performance for: ID ${childId} (Fees: ${formattedBalance} pending)`;
    } catch (err) {
      console.error('populateParentFinancialOverview error', err);
      [statBalanceEl, detailBalanceEl, lastPaymentEl].forEach(el => { if (el) el.innerText = 'Error'; });
      statDueDateEl && (statDueDateEl.innerText = 'Error');
      safeShowMessage("API Error", `Failed to fetch data for ID ${childId}: ${err.message}`, 'error');
    }
  }

  function handleChildChange(event) {
    const selectedChildId = event.target.value;
    if (selectedChildId) {
      localStorage.setItem('selectedChildId', selectedChildId);
      populateParentFinancialOverview(selectedChildId);
    } else {
      populateParentFinancialOverview(null);
    }
  }

  async function initParentPage() {
    try {
      const rawUser = getCurrentUserRaw();
      console.debug('initParentPage currentUser raw=', rawUser);
      if (!rawUser) {
        console.warn('initParentPage: no currentUser in localStorage; aborting init');
        return;
      }
      const parentId = resolveParentId(rawUser);
      console.debug('initParentPage: resolved parentId=', parentId);
      if (!parentId) {
        console.warn('initParentPage: could not resolve parent ID from currentUser; user object:', rawUser);
        safeShowMessage('Init Warning', 'Could not determine parent ID from login. Check localStorage.currentUser.', 'error');
        return;
      }

      if (rawUser.role && rawUser.role !== 'parent') {
        console.log('User not a parent. Aborting parent page init.');
        return;
      }

      const childrenSelect = document.getElementById('children-select');
      if (!childrenSelect) {
        console.warn('initParentPage: children-select element not found in DOM');
        return;
      }

      let children = (rawUser.children && Array.isArray(rawUser.children) && rawUser.children.length > 0) ? rawUser.children : null;

      try {
        if (!children) {
          children = await fetchLinkedChildren(parentId);
        }
      } catch (err) {
        // If fetch failed, log and do NOT silently use mock — show error and stop
        console.error('Could not fetch children for parent', parentId, err);
        safeShowMessage('Children Load Failed', `Could not load linked children for parent ${parentId}: ${err.message}`, 'error');
        return;
      }

      let initialChildId = localStorage.getItem('selectedChildId');

      childrenSelect.innerHTML = '<option value="">-- Select a child --</option>';
      if (!children || children.length === 0) {
        childrenSelect.innerHTML = '<option value="">No children linked</option>';
        childrenSelect.disabled = true;
        populateParentFinancialOverview(null);
        return;
      }

      children.forEach((child, idx) => {
        const option = document.createElement('option');
        const label = child.name || `${child.firstName || ''} ${child.lastName || ''}`.trim() || child.id;
        option.value = child.id;
        option.textContent = label;
        childrenSelect.appendChild(option);
        if (!initialChildId && idx === 0) initialChildId = child.id;
      });

      if (initialChildId) childrenSelect.value = initialChildId;
      populateParentFinancialOverview(childrenSelect.value || initialChildId);
      childrenSelect.addEventListener('change', handleChildChange);
    } catch (err) {
      console.error('initParentPage unexpected error', err);
      safeShowMessage('Initialization Error', 'Parent page failed to initialize: ' + (err.message || err), 'error');
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initParentPage);
  } else {
    initParentPage();
  }
})();