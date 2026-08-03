const API_BASE = "http://13.232.7.225:8080";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  });

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    throw new Error(data?.error || "Request failed");
  }

  return data;
}

export function login(name, role, password) {
  return request("/auth/login", {
    method: "POST",
    body: JSON.stringify({ name, role, password }),
  });
}

export function getAllExpenses() {
  return request("/expenses");
}

export function getUserExpenses(userId) {
  return request(`/expenses/user/${userId}`);
}

export function submitExpense(userId, amount, description) {
  return request("/expenses/submit", {
    method: "POST",
    body: JSON.stringify({ userId, amount, description }),
  });
}

export function approveExpense(expenseId, userId) {
  return request(`/expenses/${expenseId}/approve`, {
    method: "PUT",
    body: JSON.stringify({ userId }),
  });
}

export function rejectExpense(expenseId, userId) {
  return request(`/expenses/${expenseId}/reject`, {
    method: "PUT",
    body: JSON.stringify({ userId }),
  });
}

export function reimburseExpense(expenseId, userId) {
  return request(`/expenses/${expenseId}/reimburse`, {
    method: "PUT",
    body: JSON.stringify({ userId }),
  });
}
