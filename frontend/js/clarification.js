// CartMind AI - Clarification Module

window.CartMindClarification = {

  show(element, message) {
    if (!element) return;

    element.textContent = message || "";
    element.classList.add("active");
  },

  hide(element) {
    if (!element) return;

    element.textContent = "";
    element.classList.remove("active");
  },

  clear(element) {
    this.hide(element);
  },

  isEmpty(value) {
    return !String(value || "").trim();
  }

};