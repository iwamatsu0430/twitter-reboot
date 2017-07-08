riot.tag('swt-contents', '<div class="sg-contents {sg-contents-separate: isDetail}"><swt-cover if="{!isDetail && !sawitter.isLogin}"></swt-cover><swt-tweet if="{!isDetail && sawitter.isLogin}"></swt-tweet><swt-timeline if="{!isDetail}"></swt-timeline><swt-detail if="{isDetail}"></swt-detail><swt-iframe if="{isDetail}"></swt-iframe><swt-modal if="{isShowModal}"></swt-modal></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isDetail = false;
this.isShowModal = false;
// ===================================================================================
//                                                                               Event
//                                                                               =====
sawitter.obs.on("showDetail", function () {
    _this.isDetail = true;
    _this.update();
});
sawitter.obs.on("hideDetail", function () {
    _this.isDetail = false;
    _this.update();
});
sawitter.obs.on("showModal", function () {
    _this.isShowModal = true;
    _this.update();
});
sawitter.obs.on("hideModal", function () {
    setTimeout(function () {
        _this.isShowModal = false;
        _this.update();
    }, 300);
});

});
