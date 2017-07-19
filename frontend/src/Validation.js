module.exports = class Validation {

  static email(value, f) {
    const email = value.trim();
    if (email.length == 0) {
      f.call(window, "メールアドレスを入力してください");
      return;
    }
    // https://www.w3.org/TR/html5/forms.html#valid-e-mail-address
    if (!/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/.test(email)) {
      f.call(window, "正しいメールアドレスを入力してください");
      return;
    }
  }

  static password(value, f) {
    const password = value.trim();
    if (password.length == 0) {
      f.call(window, "パスワードを入力してください");
      return;
    }
    if (!/^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\d)[a-zA-Z\d]{8,32}$/.test(password)) {
      f.call(window, "8文字~32文字で半角英小大数字をそれぞれ含めてください");
      return;
    }
  }

  static passwordConfirm(value1, value2, f) {
    const password1 = value1.trim();
    const password2 = value2.trim();
    if (password1 !== password2) {
      f.call(window, "パスワードが一致しません");
      return;
    }
    Validation.password(password1, f);
    Validation.password(password2, f);
  }
}
