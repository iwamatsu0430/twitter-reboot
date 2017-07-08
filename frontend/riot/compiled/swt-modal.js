riot.tag('swt-modal', '<div class="sg-contents-modal"><div class="{sg-contents-modal-bg: isShowModal}" onclick="{closeModal}"></div><div if="{isShowModal}" class="sg-contents-modal-contents"><section><header class="sg-contents-modal-contents-header"><h1>{contents.title}</h1></header><div if="{contents.raw != null}" name="raw" class="sg-contents-modal-contents-raw"></div><div if="{contents.msg != null}">{contents.msg}</div><div if="{contents.msgSub != null}" class="sg-contents-modal-contents-msg-sub">{contents.msgSub}</div><footer class="sg-contents-modal-contents-footer"><ul><li><button onclick="{onOk}" class="sg-contents-modal-btn-ok">{contents.okButtonMsg}</button></li><li><button onclick="{onNg}" class="sg-contents-modal-btn-ng">{contents.ngButtonMsg}</button></li></ul></footer></section></div></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isShowModal = false;
this.contents = {};
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.closeModal = function (e) {
    e.preventDefault();
    sawitter.obs.trigger("hideModal");
};
this.onOk = function (e) {
    e.preventDefault();
    _this.contents.ok(_this.raw);
};
this.onNg = function (e) {
    e.preventDefault();
    _this.contents.ng(_this.raw);
};
sawitter.obs.on("showModal", function (contents) {
    _this.contents = contents;
    _this.raw.innerHTML = contents.raw;
    setTimeout(function () {
        _this.isShowModal = true;
        _this.update();
    }, 1);
});
sawitter.obs.on("hideModal", function () {
    _this.contents = {};
    _this.raw.innerHTML = "";
    _this.isShowModal = false;
    _this.update();
});

});
