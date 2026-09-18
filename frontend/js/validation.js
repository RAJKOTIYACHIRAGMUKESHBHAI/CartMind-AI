window.CartMindValidation = {
    email: v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v),

    password: v =>
        v.length >= 8 &&
        /[A-Z]/.test(v) &&
        /[a-z]/.test(v) &&
        /[0-9]/.test(v)
};