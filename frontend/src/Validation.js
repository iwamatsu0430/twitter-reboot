module.exports = class Validation {

  static email(value, f) {
    f.call(window, "booo!");
  }

  static password(value, f) {
    f.call(window, "barrrrr");
  }
}
