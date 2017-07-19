'use strict';

const Validation = require('./Validation');

const HOST = process.env.SAWTTER_BACKEND_HOST || 'http://localhost:9010';

class Sawtter {

  constructor() {
    this.doms = {
      title: document.querySelector('title'),
      video: document.querySelector('.background-video'),
      menu: document.querySelector('.menu'),
      header: document.querySelector('header'),
      logo: document.querySelector('header .logo'),
      tagline: document.querySelector('header .tagline'),
      search: document.querySelector('header .search'),
      main: document.querySelector('main'),

      loginButtons: document.querySelectorAll('a.btn-login'),
      signupButtons: document.querySelectorAll('a.btn-signup'),
      forgotButtons: document.querySelectorAll('a.btn-forgot'),

      modal: document.querySelector('.modal'),
      modalBG: document.querySelector('.modal .background'),
      loginModal: document.querySelector('.modal .login'),
      signupModal: document.querySelector('.modal .signup'),
      forgotModal: document.querySelector('.modal .forgot'),

      loginForm: document.querySelector('.modal .form-login'),
      signupForm: document.querySelector('.modal .form-signup'),
      forgotForm: document.querySelector('.modal .form-forgot')
    };

    this.doms.video.playbackRate = 0.7;
  }

  addInputed() {
    const className = 'inputed';
    this.doms.video.pause();
    this.doms.video.classList.add(className);
    this.doms.menu.classList.add(className);
    this.doms.header.classList.add(className);
    this.doms.logo.classList.add(className);
    this.doms.tagline.classList.add(className);
    this.doms.search.classList.add(className);
    this.doms.main.classList.add(className);
  }

  removeInputed() {
    const className = 'inputed';
    this.doms.video.play();
    this.doms.video.classList.remove(className);
    this.doms.menu.classList.remove(className);
    this.doms.header.classList.remove(className);
    this.doms.logo.classList.remove(className);
    this.doms.tagline.classList.remove(className);
    this.doms.search.classList.remove(className);
    this.doms.main.classList.remove(className);
  }

  clearFormClasses() {
    document.querySelectorAll('.modal input').forEach(input => {
      input.classList.remove('error');
    });
    document.querySelectorAll('.modal label').forEach(input => {
      input.classList.remove('show');
    });
  }

  addEventListeners() {
    this.doms.logo.addEventListener('click', e => {
      e.preventDefault();
      this.doms.search.value = '';
      this.removeInputed();
    });

    this.doms.search.addEventListener('input', e => {
      const className = 'inputed';
      if (e.target.value !== "") {
        this.addInputed();
      } else {
        this.removeInputed();
      }
    });

    this.doms.modalBG.addEventListener('click', e => {
      e.preventDefault();
      const className = 'show';
      this.doms.modal.classList.remove(className);
      this.doms.loginModal.classList.remove(className);
      this.doms.signupModal.classList.remove(className);
      this.doms.forgotModal.classList.remove(className);
      document.querySelectorAll('.modal input').forEach(input => {
        input.value = '';
        input.classList.remove('error');
      });
      document.querySelectorAll('.modal label').forEach(input => {
        input.innerText = '';
        input.classList.remove('show');
      });
    });

    this.doms.loginButtons.forEach(loginButton => loginButton.addEventListener('click', e => {
      e.preventDefault();
      const className = 'show';
      this.doms.modal.classList.add(className);
      this.doms.loginModal.classList.add(className);
      this.doms.signupModal.classList.remove(className);
      this.doms.forgotModal.classList.remove(className);
    }));

    this.doms.signupButtons.forEach(signupButton => signupButton.addEventListener('click', e => {
      e.preventDefault();
      const className = 'show';
      this.doms.modal.classList.add(className);
      this.doms.loginModal.classList.remove(className);
      this.doms.signupModal.classList.add(className);
      this.doms.forgotModal.classList.remove(className);
    }));

    this.doms.forgotButtons.forEach(forgotButton => forgotButton.addEventListener('click', e => {
      e.preventDefault();
      const className = 'show';
      this.doms.modal.classList.add(className);
      this.doms.loginModal.classList.remove(className);
      this.doms.signupModal.classList.remove(className);
      this.doms.forgotModal.classList.add(className);
    }));

    this.doms.loginForm.addEventListener('submit', e => {
      e.preventDefault();

      const email = e.target['email'].value;
      const password = e.target['password'].value;
      const emailInput = document.querySelector('.modal .login input[name="email"]');
      const passwordInput = document.querySelector('.modal .login input[name="password"]');
      const emailMsg = document.querySelector('.modal .login .msg-email');
      const passwordMsg = document.querySelector('.modal .login .msg-password');
      let isValid = true;

      this.clearFormClasses();
      Validation.email(email, msg => {
        isValid = false;
        emailMsg.innerText = msg;
        emailMsg.classList.add('show');
        emailInput.classList.add('error');
      });
      Validation.password(password, msg => {
        isValid = false;
        passwordMsg.innerText = msg;
        passwordMsg.classList.add('show');
        passwordInput.classList.add('error');
      });
      if (!isValid) {
        return;
      }

      // TODO Add login action
    });

    this.doms.signupForm.addEventListener('submit', e => {
      e.preventDefault();

      const email = e.target['email'].value;
      const password = e.target['password'].value;
      const passwordConfirm = e.target['password-confirm'].value;
      const emailInput = document.querySelector('.modal .signup input[name="email"]');
      const passwordInput = document.querySelector('.modal .signup input[name="password"]');
      const passwordConfirmInput = document.querySelector('.modal .signup input[name="password-confirm"]');
      const emailMsg = document.querySelector('.modal .signup .msg-email');
      const passwordMsg = document.querySelector('.modal .signup .msg-password');
      const passwordConfirmMsg = document.querySelector('.modal .signup .msg-password-confirm');
      let isValid = true;

      this.clearFormClasses();
      Validation.email(email, msg => {
        isValid = false;
        emailMsg.innerText = msg;
        emailMsg.classList.add('show');
        emailInput.classList.add('error');
      });
      Validation.password(password, msg => {
        isValid = false;
        passwordMsg.innerText = msg;
        passwordMsg.classList.add('show');
        passwordInput.classList.add('error');
      });
      Validation.passwordConfirm(password, passwordConfirm, msg => {
        isValid = false;
        passwordConfirmMsg.innerText = msg;
        passwordConfirmMsg.classList.add('show');
        passwordConfirmInput.classList.add('error');
      });

      if (!isValid) {
        return;
      }

      fetch(`${HOST}/api/auth/signup`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        mode: 'cors',
        credentials: 'include',
        body: JSON.stringify({
          email: email,
          password: password
        })
      }).then(r => console.log(r));
    });

    this.doms.forgotForm.addEventListener('submit', e => {
      e.preventDefault();

      const email = e.target['email'].value;
      const emailInput = document.querySelector('.modal .forgot input[name="email"]');
      const emailMsg = document.querySelector('.modal .forgot .msg-email');
      let isValid = true;

      this.clearFormClasses();
      Validation.email(email, msg => {
        isValid = false;
        emailMsg.innerText = msg;
        emailMsg.classList.add('show');
        emailInput.classList.add('error');
      });

      if (!isValid) {
        return;
      }

      // TODO Add forgot action
    });
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const app = new Sawtter();
  app.addEventListeners();
  fetch(`${HOST}/api/health`).then(r => console.log(r));
});
