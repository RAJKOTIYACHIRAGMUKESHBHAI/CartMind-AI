// CartMind AI - Assistant Module

window.CartMindAssistant = {

  /**
   * Add a message to the assistant chat
   */
  addMessage(container, text, type = "ai") {
    if (!container || !text) return;

    const message = document.createElement("div");
    message.className = `chat-message ${type}`;

    const bubble = document.createElement("div");
    bubble.className = "chat-bubble";
    bubble.textContent = text;

    message.appendChild(bubble);
    container.appendChild(message);

    container.scrollTop = container.scrollHeight;
  },

  /**
   * Show temporary typing/loading message
   */
  showTyping(container) {
    if (!container) return null;

    const typing = document.createElement("div");
    typing.className = "chat-message ai assistant-typing";
    typing.innerHTML = `
      <div class="chat-bubble">
        <span></span>
        <span></span>
        <span></span>
      </div>
    `;

    container.appendChild(typing);
    container.scrollTop = container.scrollHeight;

    return typing;
  },

  /**
   * Remove typing message
   */
  hideTyping(element) {
    if (element && element.parentNode) {
      element.remove();
    }
  },

  /**
   * Send query to CartMind API
   */
  async ask(query) {
    const cleanQuery = String(query || "").trim();

    if (!cleanQuery) {
      throw new Error("Please enter a shopping query.");
    }

    if (!window.CartMindAPI || typeof CartMindAPI.search !== "function") {
      throw new Error("AI service is not available.");
    }

    return await CartMindAPI.search(cleanQuery);
  }

};


// Assistant page initialization
document.addEventListener("DOMContentLoaded", () => {

  const form = document.getElementById("assistantForm");
  const input = document.getElementById("assistantInput");
  const chat = document.getElementById("assistantChat");

  if (!form || !input || !chat) return;

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const query = input.value.trim();

    if (!query) {
      input.focus();
      return;
    }

    const submitButton = form.querySelector("button[type='submit']") ||
                         form.querySelector("button");

    // User message
    CartMindAssistant.addMessage(chat, query, "user");

    input.value = "";
    input.disabled = true;

    if (submitButton) {
      submitButton.disabled = true;
      submitButton.dataset.originalText = submitButton.textContent;
      submitButton.textContent = "Thinking...";
    }

    // Typing indicator
    const typing = CartMindAssistant.showTyping(chat);

    try {

      const response = await CartMindAssistant.ask(query);

      CartMindAssistant.hideTyping(typing);

      /*
       * Do not invent product/price/rating fields.
       * The actual backend response can be mapped here
       * after its documented structure is available.
       */
      CartMindAssistant.addMessage(
        chat,
        "CartMind AI received your request successfully.",
        "ai"
      );

      // Keep the latest query available for other pages/modules
      sessionStorage.setItem("cm_q", query);

      // Keep the raw API response available for frontend processing
      try {
        sessionStorage.setItem("cm_r", JSON.stringify(response));
      } catch (storageError) {
        console.warn("Could not store API response.", storageError);
      }

    } catch (error) {

      CartMindAssistant.hideTyping(typing);

      CartMindAssistant.addMessage(
        chat,
        error.message || "Something went wrong. Please try again.",
        "ai error"
      );

    } finally {

      input.disabled = false;
      input.focus();

      if (submitButton) {
        submitButton.disabled = false;
        submitButton.textContent =
          submitButton.dataset.originalText || "Ask AI";
      }

    }
  });

});