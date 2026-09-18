// CartMind AI - Comparison Module

window.CartMindComparison = {

  setHeaders(optionA, optionB) {
    const headA = document.getElementById("headA");
    const headB = document.getElementById("headB");

    if (headA) {
      headA.textContent = optionA || "Option A";
    }

    if (headB) {
      headB.textContent = optionB || "Option B";
    }
  },

  showWorkspace() {
    const workspace = document.getElementById("workspace");

    if (!workspace) return;

    workspace.hidden = false;
    workspace.classList.add("active");
  },

  hideWorkspace() {
    const workspace = document.getElementById("workspace");

    if (!workspace) return;

    workspace.hidden = true;
    workspace.classList.remove("active");
  },

  clearComparison() {
    this.setHeaders("Option A", "Option B");
    this.hideWorkspace();
  }

};


// Compare page initialization
document.addEventListener("DOMContentLoaded", () => {

  const compareButton = document.getElementById("compareButton");

  if (!compareButton) return;

  const optionA = document.getElementById("optionA");
  const optionB = document.getElementById("optionB");

  compareButton.addEventListener("click", () => {

    const valueA = optionA ? optionA.value.trim() : "";
    const valueB = optionB ? optionB.value.trim() : "";

    if (!valueA || !valueB) {
      if (window.CartMindUI) {
        CartMindUI.alert("Please enter both options to compare.");
      } else {
        alert("Please enter both options to compare.");
      }
      return;
    }

    if (valueA.toLowerCase() === valueB.toLowerCase()) {
      if (window.CartMindUI) {
        CartMindUI.alert("Please enter two different options.");
      } else {
        alert("Please enter two different options.");
      }
      return;
    }

    CartMindComparison.setHeaders(valueA, valueB);
    CartMindComparison.showWorkspace();

  });

});