import React, { useEffect, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  approveExpense,
  getAllExpenses,
  getUserExpenses,
  login,
  rejectExpense,
  reimburseExpense,
  submitExpense,
} from "./api";
import "./styles.css";

function App() {
  const [user, setUser] = useState(null);
  const [name, setName] = useState("");
  const [role, setRole] = useState("EMPLOYEE");
  const [password, setPassword] = useState("");
  const [expenses, setExpenses] = useState([]);
  const [amount, setAmount] = useState("");
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      loadExpenses(user);
    }
  }, [user]);

  async function handleLogin(event) {
    event.preventDefault();
    run(async () => {
      const loggedInUser = await login(name, role, password);
      setUser(loggedInUser);
      setPassword("");
      setMessage(
        loggedInUser.firstLogin
          ? `User ready. Entered as ${loggedInUser.role}.`
          : `Logged in as ${loggedInUser.role}.`
      );
    });
  }

  async function loadExpenses(currentUser = user) {
    if (!currentUser) return;

    run(async () => {
      const data =
        currentUser.role === "EMPLOYEE"
          ? await getUserExpenses(currentUser.id)
          : await getAllExpenses();
      setExpenses(data);
    }, false);
  }

  async function handleSubmit(event) {
    event.preventDefault();
    run(async () => {
      await submitExpense(user.id, amount, description);
      setAmount("");
      setDescription("");
      setMessage("Expense submitted.");
      await loadExpenses();
    });
  }

  async function handleAction(action, expenseId) {
    run(async () => {
      if (action === "approve") await approveExpense(expenseId, user.id);
      if (action === "reject") await rejectExpense(expenseId, user.id);
      if (action === "reimburse") await reimburseExpense(expenseId, user.id);
      setMessage("Expense updated.");
      await loadExpenses();
    });
  }

  async function run(task, showLoading = true) {
    try {
      if (showLoading) setLoading(true);
      setMessage("");
      await task();
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  if (!user) {
    return (
      <main className="page login-page">
        <section className="login-panel">
          <h1>Expense Approval System</h1>
          <form onSubmit={handleLogin} className="form">
            <label>
              Name
              <input
                value={name}
                onChange={(event) => setName(event.target.value)}
                placeholder="Enter your name"
                autoFocus
              />
            </label>
            <label>
              Role
              <select value={role} onChange={(event) => setRole(event.target.value)}>
                <option value="EMPLOYEE">Employee</option>
                <option value="MANAGER">Manager</option>
                <option value="FINANCE">Finance</option>
              </select>
            </label>
            <label>
              Password
              <input
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="First login saves this password"
                type="password"
              />
            </label>
            <p className="hint">
              Existing users must choose their saved role. New names are added with the selected role.
            </p>
            <button disabled={loading}>Login</button>
          </form>
          {message && <p className="message">{message}</p>}
        </section>
      </main>
    );
  }

  return (
    <main className="page">
      <header className="topbar">
        <div>
          <h1>Expense Approval System</h1>
          <p>
            {user.name} - {user.role}
          </p>
        </div>
        <div className="actions">
          <button className="secondary" onClick={() => loadExpenses()} disabled={loading}>
            Refresh
          </button>
          <button className="secondary" onClick={() => setUser(null)}>
            Logout
          </button>
        </div>
      </header>

      {message && <p className="message">{message}</p>}

      {user.role === "EMPLOYEE" && (
        <section className="panel">
          <h2>Submit Expense</h2>
          <form onSubmit={handleSubmit} className="expense-form">
            <label>
              Amount
              <input
                value={amount}
                onChange={(event) => setAmount(event.target.value)}
                type="number"
                min="1"
                step="0.01"
              />
            </label>
            <label>
              Description
              <input
                value={description}
                onChange={(event) => setDescription(event.target.value)}
              />
            </label>
            <button disabled={loading}>Submit</button>
          </form>
        </section>
      )}

      <section className="panel">
        <h2>Expenses</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Amount</th>
                <th>Description</th>
                <th>Status</th>
                <th>Submitted By</th>
                <th>Approved By</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {expenses.length === 0 && (
                <tr>
                  <td colSpan="7" className="empty">
                    No expenses found.
                  </td>
                </tr>
              )}
              {expenses.map((expense) => (
                <tr key={expense.id}>
                  <td>{expense.id}</td>
                  <td>{expense.amount}</td>
                  <td>{expense.description}</td>
                  <td>
                    <span className={`status ${expense.status.toLowerCase()}`}>
                      {expense.status}
                    </span>
                  </td>
                  <td>{expense.submittedBy?.name}</td>
                  <td>{expense.approvedBy?.name || "-"}</td>
                  <td>
                    <ActionButtons user={user} expense={expense} onAction={handleAction} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  );
}

function ActionButtons({ user, expense, onAction }) {
  if (user.role === "MANAGER" && expense.status === "SUBMITTED") {
    return (
      <div className="row-actions">
        <button onClick={() => onAction("approve", expense.id)}>Approve</button>
        <button className="danger" onClick={() => onAction("reject", expense.id)}>
          Reject
        </button>
      </div>
    );
  }

  if (user.role === "FINANCE" && expense.status === "APPROVED") {
    return <button onClick={() => onAction("reimburse", expense.id)}>Reimburse</button>;
  }

  return <span className="muted">No action</span>;
}

createRoot(document.getElementById("root")).render(<App />);
