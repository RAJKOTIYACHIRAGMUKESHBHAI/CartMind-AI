// CartMind AI - Modal Module

window.CartMindModal = {

  open(modal) {
    if (!modal) return;

    modal.classList.add("active");
    modal.removeAttribute("hidden");

    document.body.classList.add("modal-open");
  },

  close(modal) {
    if (!modal) return;

    modal.classList.remove("active");
    modal.setAttribute("hidden", "");

    document.body.classList.remove("modal-open");
  },

  toggle(modal) {
    if (!modal) return;

    if (modal.classList.contains("active")) {
      this.close(modal);
    } else {
      this.open(modal);
    }
  }

};


// Modal initialization
document.addEventListener("DOMContentLoaded", () => {

  const openButtons = document.querySelectorAll("[data-modal-open]");
  const closeButtons = document.querySelectorAll("[data-modal-close]");

  openButtons.forEach((button) => {

    button.addEventListener("click", () => {

      const targetId = button.getAttribute("data-modal-open");

      if (!targetId) return;

      const modal = document.getElementById(targetId);

      CartMindModal.open(modal);

    });

  });


  closeButtons.forEach((button) => {

    button.addEventListener("click", () => {

      const modal = button.closest(".modal");

      CartMindModal.close(modal);

    });

  });


  document.querySelectorAll(".modal").forEach((modal) => {

    modal.addEventListener("click", (event) => {

      if (event.target === modal) {
        CartMindModal.close(modal);
      }

    });

  });


  // Close modal with Escape key
  document.addEventListener("keydown", (event) => {

    if (event.key !== "Escape") return;

    document.querySelectorAll(".modal.active").forEach((modal) => {
      CartMindModal.close(modal);
    });

  });

});