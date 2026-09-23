// CartMind AI - Utility Module

window.CartMindUtils = {

  // Get element safely
  $(selector, parent = document) {
    return parent.querySelector(selector);
  },

  // Get multiple elements
  $$(selector, parent = document) {
    return Array.from(parent.querySelectorAll(selector));
  },

  // Trim text safely
  cleanText(value) {
    return String(value ?? "").trim();
  },

  // Check whether a value is empty
  isEmpty(value) {
    return this.cleanText(value).length === 0;
  },

  // Escape HTML to prevent unwanted markup injection
  escapeHTML(value) {
    const div = document.createElement("div");
    div.textContent = String(value ?? "");
    return div.innerHTML;
  },

  // Create an element with optional class and text
  createElement(tag, className = "", text = "") {
    const element = document.createElement(tag);

    if (className) {
      element.className = className;
    }

    if (text) {
      element.textContent = text;
    }

    return element;
  },

  // Format a date for display
  formatDate(dateValue) {
    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
      return "";
    }

    return date.toLocaleDateString();
  },

  // Safely parse JSON
  parseJSON(value, fallback = null) {
    try {
      return JSON.parse(value);
    } catch (error) {
      return fallback;
    }
  },

  // Small delay utility
  wait(milliseconds = 0) {
    return new Promise((resolve) => {
      setTimeout(resolve, milliseconds);
    });
  }

};