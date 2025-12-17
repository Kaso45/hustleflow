// src/services/mocks/timesheet.mock.js
// Mock timesheet service
// - Backend-first
// - Safe override simulation
// - No fake logic in GET

const mockData = [
  {
    id: 501,
    employeeId: 1,
    employeeName: "John Doe",
    date: "2025-11-18",
    checkIn: "08:00",
    checkOut: "17:30",
    totalHours: 8.5,
    status: "ON_TIME",
    overridden: false,
    overriddenAt: null
  },
  {
    id: 502,
    employeeId: 1,
    employeeName: "John Doe",
    date: "2025-11-17",
    checkIn: "08:15",
    checkOut: "17:10",
    totalHours: 7.9,
    status: "LATE",
    overridden: false,
    overriddenAt: null
  },
  {
    id: 503,
    employeeId: 2,
    employeeName: "Jane Smith",
    date: "2025-11-16",
    checkIn: null,
    checkOut: null,
    totalHours: 0,
    status: "ABSENT",
    overridden: false,
    overriddenAt: null
  }
];

/* ===================== HELPERS ===================== */

function normalizeTime(t) {
  if (!t) return null;
  return t.length >= 5 ? t.slice(0, 5) : t;
}

/**
 * Compute system status
 * NEVER overridden here
 */
function computeStatus(record) {
  const checkIn = record.checkIn;
  if (!checkIn) return "ABSENT";

  const [h, m] = checkIn.split(":").map(Number);
  const minutes = h * 60 + m;
  const startMinutes = 8 * 60;
  const lateCutoff = startMinutes + 5;

  return minutes <= lateCutoff ? "ON_TIME" : "LATE";
}

/* ===================== API ===================== */

export async function GET_timesheets(params = {}) {
  await new Promise(r => setTimeout(r, 120));

  let list = mockData.map(r => ({
    ...r,
    checkIn: normalizeTime(r.checkIn),
    checkOut: normalizeTime(r.checkOut)
  }));

  if (params.employeeId) {
    list = list.filter(
      r => Number(r.employeeId) === Number(params.employeeId)
    );
  }

  if (params.month && params.year) {
    list = list.filter(r => {
      const d = new Date(r.date);
      return (
        d.getMonth() + 1 === Number(params.month) &&
        d.getFullYear() === Number(params.year)
      );
    });
  }

  /**
   * IMPORTANT:
   * - Status returned is always backend-computed OR overridden value
   * - Frontend decides whether to trust overridden or not
   */
  return JSON.parse(JSON.stringify(list));
}

export async function POST_clockIn(payload = {}) {
  await new Promise(r => setTimeout(r, 100));

  const ts = payload.timestamp || new Date().toISOString();
  const date = ts.split("T")[0];
  const time = ts.slice(11, 16);

  const existing = mockData.find(
    x => x.employeeId === Number(payload.employeeId) && x.date === date
  );

  if (existing) {
    existing.checkIn = time;
    existing.status = computeStatus(existing);
    existing.overridden = false;
    existing.overriddenAt = null;

    return { message: "Updated clock-in (mock)", data: existing };
  }

  const newRec = {
    id: mockData.length + 500,
    employeeId: Number(payload.employeeId),
    employeeName: payload.employeeName || `Employee ${payload.employeeId}`,
    date,
    checkIn: time,
    checkOut: null,
    totalHours: 0,
    status: "ON_TIME",
    overridden: false,
    overriddenAt: null
  };

  mockData.push(newRec);
  return { message: "Mock clock-in created", data: newRec };
}

export async function PATCH_clockOut(payload = {}) {
  await new Promise(r => setTimeout(r, 100));

  const ts = payload.timestamp || new Date().toISOString();
  const date = ts.split("T")[0];
  const time = ts.slice(11, 16);

  const rec = mockData.find(
    x => x.employeeId === Number(payload.employeeId) && x.date === date
  );

  if (!rec) return null;

  rec.checkOut = time;

  if (rec.checkIn) {
    const [h1, m1] = rec.checkIn.split(":").map(Number);
    const [h2, m2] = rec.checkOut.split(":").map(Number);
    rec.totalHours = Number(
      Math.max(0, h2 + m2 / 60 - (h1 + m1 / 60)).toFixed(1)
    );
  } else {
    rec.totalHours = 0;
  }

  rec.status = computeStatus(rec);
  rec.overridden = false;
  rec.overriddenAt = null;

  return { message: "Mock clock-out updated", data: rec };
}

/**
 * SAFE OVERRIDE – SIMULATION ONLY
 * Must be protected by feature flag in frontend
 */
export async function PATCH_overrideStatus({ timesheetId, status }) {
  await new Promise(r => setTimeout(r, 100));

  const rec = mockData.find(r => r.id === timesheetId);
  if (!rec) return null;

  rec.status = status;
  rec.overridden = true;
  rec.overriddenAt = new Date().toISOString();

  return {
    message: "Mock override status applied",
    data: rec
  };
}

/* ===================== AUTO GENERATE ===================== */

for (let i = 1; i <= 15; i++) {
  if (!mockData.find(r => r.employeeId === i)) {
    mockData.push({
      id: 600 + i,
      employeeId: i,
      employeeName: `Employee ${i}`,
      date: "2025-11-18",
      checkIn: "08:00",
      checkOut: "17:00",
      totalHours: 9,
      status: i % 4 === 0 ? "LATE" : "ON_TIME",
      overridden: false,
      overriddenAt: null
    });
  }
}

export default {
  GET_timesheets,
  POST_clockIn,
  PATCH_clockOut,
  PATCH_overrideStatus
};
