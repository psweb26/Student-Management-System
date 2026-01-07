// profile-populate.js - cleaned single-version file
const API_BASE_URL = 'http://localhost:8080/api/v1';

function getAuthToken() {
  return localStorage.getItem('authToken');
}

function getCurrentUserObj() {
  try {
    return JSON.parse(localStorage.getItem('currentUser') || 'null');
  } catch (e) {
    return null;
  }
}

function buildAuthHeaders() {
  const headers = { 'Content-Type': 'application/json', 'Accept': 'application/json' };
  const token = getAuthToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;
  return headers;
}

async function fetchJson(url, opts = {}) {
  const headers = Object.assign({}, buildAuthHeaders(), opts.headers || {});
  const res = await fetch(url, Object.assign({ headers, credentials: 'include' }, opts));
  return res;
}

async function fetchStudent(studentId) {
  const resp = await fetchJson(`${API_BASE_URL}/students/${encodeURIComponent(studentId)}`);
  if (!resp.ok) throw new Error('Failed to fetch student: ' + resp.status);
  return resp.json();
}

function setText(id, text) {
  const el = document.getElementById(id);
  if (el) el.textContent = text ?? '';
}

async function populateProfile() {
  const currentUser = getCurrentUserObj();
  if (!currentUser || !currentUser.id) {
    window.location.href = 'index.html';
    return;
  }

  try {
    let student;
    try {
      student = await fetchStudent(currentUser.id);
    } catch (e) {
      console.warn('fetch student failed, falling back to local currentUser', e);
      student = currentUser;
    }

    setText('profile-fullname', `${student.firstName || ''} ${student.lastName || ''}`);
    setText('profile-id', student.id || student.studentId || '');
    setText('profile-email', student.email || '');
    setText('profile-phone', student.phoneNumber || student.phone_number || '');
    setText('profile-dob', student.dateOfBirth || student.date_of_birth || '');
    setText('profile-major', student.major || '');
    setText('profile-program', student.program || 'B.Tech');
    setText('profile-advisor', student.advisor || '');

    if (student.gpa !== undefined) setText('profile-gpa', student.gpa);
    if (student.creditsEarned !== undefined) setText('profile-credits', `${student.creditsEarned}/${student.creditsTotal || 120}`);

  } catch (err) {
    console.error('populateProfile error', err);
  }
}

async function fetchEnrollments(studentId) {
  const resp = await fetchJson(`${API_BASE_URL}/enrollments/student/${encodeURIComponent(studentId)}`);
  if (!resp.ok) return [];
  return resp.json();
}

async function fetchFees(studentId) {
  const resp = await fetchJson(`${API_BASE_URL}/fees/student/${encodeURIComponent(studentId)}`);
  if (!resp.ok) return [];
  return resp.json();
}

function gradeToPoints(grade) {
  switch (grade) {
    case 'A+':
    case 'A': return 4.0;
    case 'B+': return 3.0;
    case 'B': return 2.0;
    case 'C': return 1.0;
    default: return 0.0;
  }
}

async function fetchEnrollmentsAndComputeGPA(studentId) {
  try {
    const rows = await fetchEnrollments(studentId);
    let totalPoints = 0;
    let totalCreditsForGpa = 0;
    let creditsEarned = 0;
    for (const r of rows) {
      const credits = Number(r.credits ?? r.courseCredits ?? r.course_credits ?? 0);
      const grade = r.grade ?? null;
      if (!isNaN(credits) && credits > 0) {
        if (grade && grade !== 'N/A' && grade !== 'F' && grade !== '') creditsEarned += credits;
        const points = gradeToPoints(grade);
        totalPoints += points * credits;
        if (points > 0) totalCreditsForGpa += credits;
      }
    }
    const gpa = totalCreditsForGpa > 0 ? (totalPoints / totalCreditsForGpa) : null;
    return { gpa, creditsEarned };
  } catch {
    return { gpa: null, creditsEarned: 0 };
  }
}

async function fetchFeeStatus(studentId) {
  try {
    const fees = await fetchFees(studentId);
    const totalPending = (fees || []).filter(f => f.status !== 'Paid').reduce((s,f)=>s+parseFloat(f.amount||0),0);
    const pendingRecord = (fees || []).filter(f => f.status !== 'Paid').sort((a,b) => new Date(a.dueDate) - new Date(b.dueDate))[0];
    let status = 'PAID';
    if ((fees || []).some(f => f.status === 'Overdue')) status = 'OVERDUE';
    else if ((fees || []).some(f => f.status === 'Pending') || totalPending > 0) status = 'OUTSTANDING';
    return { totalPending, nextDueDate: pendingRecord ? pendingRecord.dueDate : null, status };
  } catch {
    return { totalPending: 0, nextDueDate: null, status: 'ERROR' };
  }
}

function formatCurrencyINR(amount) {
  try { return '₹ ' + (Number(amount) || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 }); }
  catch { return '₹ 0.00'; }
}

async function refreshComputedSections(studentId) {
  try {
    const { gpa, creditsEarned } = await fetchEnrollmentsAndComputeGPA(studentId);
    document.getElementById('profile-gpa').innerText = (gpa !== null && !isNaN(gpa)) ? gpa.toFixed(2) : 'N/A';
    document.getElementById('profile-credits').innerText = `${creditsEarned ?? 0}/120`;
  } catch {}
  try {
    const feeInfo = await fetchFeeStatus(studentId);
    const feeStatusEl = document.getElementById('fee-status');
    const dueAmountEl = document.getElementById('fee-due-amount');
    const dueDateEl = document.getElementById('fee-due-date');
    if (dueAmountEl) dueAmountEl.innerText = formatCurrencyINR(feeInfo.totalPending ?? 0);
    if (dueDateEl) dueDateEl.innerText = feeInfo.nextDueDate ? new Date(feeInfo.nextDueDate).toLocaleDateString('en-US', { day:'numeric', month:'long', year:'numeric' }) : 'N/A';
    if (feeStatusEl) {
      if (feeInfo.status === 'OVERDUE') {
        feeStatusEl.innerText = 'Overdue'; feeStatusEl.className = 'text-red-700 font-bold text-lg';
      } else if (feeInfo.status === 'OUTSTANDING') {
        feeStatusEl.innerText = 'Outstanding'; feeStatusEl.className = 'text-yellow-700 font-bold text-lg';
      } else {
        feeStatusEl.innerText = 'Paid Up'; feeStatusEl.className = 'text-green-700 font-bold text-lg';
      }
    }
  } catch {}
}

// Initialize from DOMContentLoaded (if profile page includes this file)
document.addEventListener('DOMContentLoaded', async () => {
  // Only run if profile page elements exist
  if (!document.getElementById('profile-fullname')) return;
  const user = getCurrentUserObj();
  if (!user || !user.id) { window.location.href = 'login.html'; return; }
  if (user.role && user.role !== 'student') { window.showMessage?.('Access Denied','This page is for students only.','error'); return; }
  const studentId = user.id;
  try {
    const studentData = await fetchStudent(studentId);
    window.currentStudentData = studentData;
    const fullName = `${studentData.firstName || ''} ${studentData.lastName || ''}`.trim();
    document.getElementById('profile-avatar-initials').innerText = (studentData.firstName?studentData.firstName.charAt(0):'') + (studentData.lastName?studentData.lastName.charAt(0):'');
    document.getElementById('profile-fullname').innerText = fullName || 'Student Name';
    document.getElementById('profile-id').innerText = studentData.id || 'N/A';
    document.getElementById('profile-email').innerText = studentData.email || 'N/A';
    document.getElementById('profile-phone').innerText = studentData.phoneNumber || studentData.phone || 'N/A';
    document.getElementById('profile-dob').innerText = studentData.dateOfBirth || 'N/A';
    document.getElementById('profile-address').innerText = studentData.address || 'N/A';
    document.getElementById('profile-major').innerText = studentData.major || 'N/A';
    document.getElementById('profile-program').innerText = studentData.program || 'B.Tech';
    document.getElementById('profile-year').innerText = studentData.year || 'N/A';
    document.getElementById('profile-advisor').innerText = studentData.advisor || 'N/A';
  } catch (err) {
    console.error('Initial profile fetch failed:', err);
  }
  await refreshComputedSections(studentId);
  const logoutLink = document.getElementById('logout-link');
  if (logoutLink) {
    logoutLink.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('currentUser');
      localStorage.removeItem('authToken');
      window.location.href = 'login.html';
    });
  }
});