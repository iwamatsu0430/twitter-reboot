(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
// shim for using process in browser
var process = module.exports = {};

// cached from whatever global is present so that test runners that stub it
// don't break things.  But we need to wrap it in a try catch in case it is
// wrapped in strict mode code which doesn't define any globals.  It's inside a
// function because try/catches deoptimize in certain engines.

var cachedSetTimeout;
var cachedClearTimeout;

function defaultSetTimout() {
    throw new Error('setTimeout has not been defined');
}
function defaultClearTimeout () {
    throw new Error('clearTimeout has not been defined');
}
(function () {
    try {
        if (typeof setTimeout === 'function') {
            cachedSetTimeout = setTimeout;
        } else {
            cachedSetTimeout = defaultSetTimout;
        }
    } catch (e) {
        cachedSetTimeout = defaultSetTimout;
    }
    try {
        if (typeof clearTimeout === 'function') {
            cachedClearTimeout = clearTimeout;
        } else {
            cachedClearTimeout = defaultClearTimeout;
        }
    } catch (e) {
        cachedClearTimeout = defaultClearTimeout;
    }
} ())
function runTimeout(fun) {
    if (cachedSetTimeout === setTimeout) {
        //normal enviroments in sane situations
        return setTimeout(fun, 0);
    }
    // if setTimeout wasn't available but was latter defined
    if ((cachedSetTimeout === defaultSetTimout || !cachedSetTimeout) && setTimeout) {
        cachedSetTimeout = setTimeout;
        return setTimeout(fun, 0);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedSetTimeout(fun, 0);
    } catch(e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't trust the global object when called normally
            return cachedSetTimeout.call(null, fun, 0);
        } catch(e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error
            return cachedSetTimeout.call(this, fun, 0);
        }
    }


}
function runClearTimeout(marker) {
    if (cachedClearTimeout === clearTimeout) {
        //normal enviroments in sane situations
        return clearTimeout(marker);
    }
    // if clearTimeout wasn't available but was latter defined
    if ((cachedClearTimeout === defaultClearTimeout || !cachedClearTimeout) && clearTimeout) {
        cachedClearTimeout = clearTimeout;
        return clearTimeout(marker);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedClearTimeout(marker);
    } catch (e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't  trust the global object when called normally
            return cachedClearTimeout.call(null, marker);
        } catch (e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error.
            // Some versions of I.E. have different rules for clearTimeout vs setTimeout
            return cachedClearTimeout.call(this, marker);
        }
    }



}
var queue = [];
var draining = false;
var currentQueue;
var queueIndex = -1;

function cleanUpNextTick() {
    if (!draining || !currentQueue) {
        return;
    }
    draining = false;
    if (currentQueue.length) {
        queue = currentQueue.concat(queue);
    } else {
        queueIndex = -1;
    }
    if (queue.length) {
        drainQueue();
    }
}

function drainQueue() {
    if (draining) {
        return;
    }
    var timeout = runTimeout(cleanUpNextTick);
    draining = true;

    var len = queue.length;
    while(len) {
        currentQueue = queue;
        queue = [];
        while (++queueIndex < len) {
            if (currentQueue) {
                currentQueue[queueIndex].run();
            }
        }
        queueIndex = -1;
        len = queue.length;
    }
    currentQueue = null;
    draining = false;
    runClearTimeout(timeout);
}

process.nextTick = function (fun) {
    var args = new Array(arguments.length - 1);
    if (arguments.length > 1) {
        for (var i = 1; i < arguments.length; i++) {
            args[i - 1] = arguments[i];
        }
    }
    queue.push(new Item(fun, args));
    if (queue.length === 1 && !draining) {
        runTimeout(drainQueue);
    }
};

// v8 likes predictible objects
function Item(fun, array) {
    this.fun = fun;
    this.array = array;
}
Item.prototype.run = function () {
    this.fun.apply(null, this.array);
};
process.title = 'browser';
process.browser = true;
process.env = {};
process.argv = [];
process.version = ''; // empty string to avoid regexp issues
process.versions = {};

function noop() {}

process.on = noop;
process.addListener = noop;
process.once = noop;
process.off = noop;
process.removeListener = noop;
process.removeAllListeners = noop;
process.emit = noop;
process.prependListener = noop;
process.prependOnceListener = noop;

process.listeners = function (name) { return [] }

process.binding = function (name) {
    throw new Error('process.binding is not supported');
};

process.cwd = function () { return '/' };
process.chdir = function (dir) {
    throw new Error('process.chdir is not supported');
};
process.umask = function() { return 0; };

},{}],2:[function(require,module,exports){
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

},{}],3:[function(require,module,exports){
(function (process){
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
    fetch(`${HOST}/api/page/comments/${encodeURIComponent(url)}`, {
      mode: 'cors',
      credentials: 'include'
    })
      .then(r => r.json())
      .then(comments => {
        // TODO
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
      const url = e.target.value;
      if (url !== "") {
        this.addInputed();
        this.loadIframe(url);
        this.loadComments(url);
      } else {
        this.removeInputed();
      }
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

}).call(this,require('_process'))
},{"./Validation":2,"_process":1}]},{},[3]);
