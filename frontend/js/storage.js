// CartMind AI - Storage Module

window.CartMindStorage = {

  set(key, value) {
    if (!key) return false;

    try {
      sessionStorage.setItem(key, JSON.stringify(value));
      return true;
    } catch (error) {
      console.warn("Storage write failed.");
      return false;
    }
  },

  get(key, defaultValue = null) {
    if (!key) return defaultValue;

    try {
      const value = sessionStorage.getItem(key);

      if (value === null) {
        return defaultValue;
      }

      return JSON.parse(value);

    } catch (error) {
      console.warn("Storage read failed.");
      return defaultValue;
    }
  },

  remove(key) {
    if (!key) return;

    try {
      sessionStorage.removeItem(key);
    } catch (error) {
      console.warn("Storage remove failed.");
    }
  },

  clear() {
    try {
      sessionStorage.clear();
    } catch (error) {
      console.warn("Storage clear failed.");
    }
  }

};