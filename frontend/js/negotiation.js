// CartMind AI - Negotiation Module

window.CartMindNegotiation = {

  createRequest(message) {
    const value = String(message || "").trim();

    if (!value) {
      return null;
    }

    return {
      message: value,
      createdAt: new Date().toISOString()
    };
  },

  validate(message) {
    return String(message || "").trim().length > 0;
  },

  clear(input) {
    if (!input) return;

    input.value = "";
    input.focus();
  }

};


// Negotiation box initialization
document.addEventListener("DOMContentLoaded", () => {

  const form = document.getElementById("negotiationForm");

  if (!form) return;

  const input =
    document.getElementById("negotiationInput") ||
    form.querySelector("input, textarea");

  form.addEventListener("submit", (event) => {

    event.preventDefault();

    const message = input ? input.value.trim() : "";

    if (!CartMindNegotiation.validate(message)) {
      if (window.CartMindUI) {
        CartMindUI.alert("Please enter your request.");
      } else {
        alert("Please enter your request.");
      }
      return;
    }

    const request = CartMindNegotiation.createRequest(message);

    if (!request) return;

    // Keep the request available for future API integration.
    try {
      sessionStorage.setItem(
        "cm_negotiation",
        JSON.stringify(request)
      );
    } catch (error) {
      console.warn("Could not store negotiation request.");
    }

    CartMindNegotiation.clear(input);

  });

});