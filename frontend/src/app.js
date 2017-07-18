'use strict';

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
      console.log(this.doms.forgotModal);
      this.doms.forgotModal.classList.add(className);
    }));

    this.doms.loginForm.addEventListener('submit', e => {
      e.preventDefault();
    });

    this.doms.signupForm.addEventListener('submit', e => {
      e.preventDefault();
    });

    this.doms.forgotForm.addEventListener('submit', e => {
      e.preventDefault();
    });
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const app = new Sawtter();
  app.addEventListeners();
  fetch(`${HOST}/api/health`).then(r => console.log(r));
});
