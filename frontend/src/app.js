'use strict';

const Validation = require('./Validation');

const HOST = process.env.SAWTTER_BACKEND_HOST || 'http://localhost:9010';

class Sawtter {

  constructor() {
    this.user = {};
    this.doms = {
      title: document.querySelector('title'),
      video: document.querySelector('.background-video'),
      menu: document.querySelector('.menu'),
      header: document.querySelector('header'),
      logo: document.querySelector('header .logo'),
      tagline: document.querySelector('header .tagline'),
      search: document.querySelector('header .search'),
      main: document.querySelector('main'),
      iframe: document.querySelector('.content iframe'),

      loginButtons: document.querySelectorAll('a.btn-login'),
      logoutButtons: document.querySelectorAll('a.btn-logout'),
      signupButtons: document.querySelectorAll('a.btn-signup'),
      forgotButtons: document.querySelectorAll('a.btn-forgot'),

      modal: document.querySelector('.modal'),
      modalBG: document.querySelector('.modal .background'),
      loginModal: document.querySelector('.modal .login'),
      signupModal: document.querySelector('.modal .signup'),
      forgotModal: document.querySelector('.modal .forgot'),

      loginForm: document.querySelector('.modal .form-login'),
      signupForm: document.querySelector('.modal .form-signup'),
      forgotForm: document.querySelector('.modal .form-forgot'),

      loginedElements: document.querySelectorAll('.switch-logined'),
      logoutedElements: document.querySelectorAll('.switch-logouted'),

      commentForm: document.querySelector('.comments .actions form'),
      commentButton: document.querySelector('.comments .actions button'),
      commentTextArea: document.querySelector('.comments .actions form textarea'),
      commentTextCount: document.querySelector('.comments .actions form span'),

      commentParent: document.querySelector('.comments ul'),
      commentBase: (() => {
        const dom = document.querySelector('.comments ul .comment');
        const cloned = dom.cloneNode(true);
        dom.remove();
        return cloned;
      })(),

      loading: document.querySelector('.loading'),
      alert: document.querySelector('.alert')
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

  hideModal() {
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
      input.classList.remove(className);
    });
  }

  showLoading() {
    const className = 'show';
    this.doms.loading.classList.add(className);
  }

  hideLoading() {
    const className = 'show';
    this.doms.loading.classList.remove(className);
  }

  clearFormClasses() {
    document.querySelectorAll('.modal input').forEach(input => {
      input.classList.remove('error');
    });
    document.querySelectorAll('.modal label').forEach(input => {
      input.classList.remove('show');
    });
  }

  showAlert(msg) {
    this.doms.alert.children[0].innerText = msg;
    this.doms.alert.classList.add('show');
    setTimeout(() => {
      this.doms.alert.classList.remove('show');
      this.doms.alert.children[0].innerText = '';
    }, 3000);
  }

  loadIframe(url) {
    fetch(`${HOST}/api/page/caniframe/${encodeURIComponent(url)}`, {
      mode: 'cors',
      credentials: 'include'
    })
      .then(r => r.json())
      .then(canIframe => {
        if (canIframe) {
          this.doms.iframe.src = url;
        } else {
          // TODO Load page img
        }
      });
  }

  loadComments(url) {
    document.querySelectorAll('.comments ul .comment').forEach(c => c.remove());
    fetch(`${HOST}/api/page/comment/${encodeURIComponent(url)}`, {
      mode: 'cors',
      credentials: 'include'
    })
      .then(r => r.json())
      .then(comments => {
        comments.forEach(c => {
          const newComment = this.doms.commentBase.cloneNode(true);
          newComment.querySelector('.comment-content p').innerText = c.text;
          newComment.querySelector('.comment-content time').innerText = c.createdAt;
          this.doms.commentParent.appendChild(newComment);
        });
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
      const url = e.target.value.trim();
      if (url !== "") {
        this.addInputed();
        this.loadIframe(url);
        this.loadComments(url);
      } else {
        this.removeInputed();
      }
    });

    this.doms.commentForm.addEventListener('submit', e => {
      e.preventDefault();
      const url = this.doms.search.value.trim();
      const textarea = e.target.querySelector('textarea');
      const comment = textarea.value.trim();
      if (comment.length < 0 || comment.length > 140) {
        return;
      }
      this.showLoading();
      fetch(`${HOST}/api/page/comment/${encodeURIComponent(url)}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        mode: 'cors',
        credentials: 'include',
        body: JSON.stringify({
          text: comment
        })
      }).then(r => {
        textarea.value = '';
        this.hideLoading();
        this.loadComments(url);
      });
    });

    this.doms.modalBG.addEventListener('click', e => {
      e.preventDefault();
      this.hideModal();
    });

    this.doms.loginButtons.forEach(loginButton => loginButton.addEventListener('click', e => {
      e.preventDefault();
      const className = 'show';
      this.doms.modal.classList.add(className);
      this.doms.loginModal.classList.add(className);
      this.doms.signupModal.classList.remove(className);
      this.doms.forgotModal.classList.remove(className);
    }));

    this.doms.logoutButtons.forEach(logoutButton => logoutButton.addEventListener('click', e => {
      e.preventDefault();

      fetch(`${HOST}/api/auth/logout`, {
        mode: 'cors',
        credentials: 'include'
      }).then(r => {
        if (r.status === 200) {
          this.fetchUserInfo();
        } else {
          // TODO
        }
      });
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

      this.showLoading();
      fetch(`${HOST}/api/auth/login`, {
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
      }).then(r => {
        if (r.status === 200) {
          this.fetchUserInfo();
        } else {
          this.showAlert(`認証に失敗しました。`);
        }
        this.hideModal();
        this.hideLoading();
      });
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

      this.showLoading();
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
      }).then(r => {
        if (r.status === 200) {
          this.showAlert(`メールアドレス「${email}」に登録確認メールを送信しました。`);
          this.hideModal();
          this.hideLoading();
        } else {
          // TODO
        }
      });
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

    this.doms.commentTextArea.addEventListener('input', e => {
      const className = 'error';
      const count = 140 - e.target.value.length;
      this.doms.commentTextCount.innerText = count;
      if (count < 0) {
        this.doms.commentTextCount.classList.add(className);
        this.doms.commentButton.disabled = true;
        this.doms.commentButton.classList.add(className);
      } else {
        this.doms.commentTextCount.classList.remove(className);
        this.doms.commentButton.disabled = false;
        this.doms.commentButton.classList.remove(className);
      }
    });
  }

  fetchUserInfo() {
    fetch(`${HOST}/api/user/me`, {
      mode: 'cors',
      credentials: 'include'
    }).then(r => {
      const className = 'show';
      if (r.status === 200) {
        this.doms.loginedElements.forEach(e => e.classList.add(className));
        this.doms.logoutedElements.forEach(e => e.classList.remove(className));
      } else if (r.status === 401) {
        this.doms.loginedElements.forEach(e => e.classList.remove(className));
        this.doms.logoutedElements.forEach(e => e.classList.add(className));
      }
    })
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const app = new Sawtter();
  app.addEventListeners();
  app.fetchUserInfo();
});
