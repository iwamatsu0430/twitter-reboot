riot.tag('swt-iframe', '<iframe class="sg-contents-iframe" name="contentsIframe"></iframe>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                               Event
//                                                                               =====
sawitter.obs.on("onContentsLoaded", function (contents) {
    _this.contentsIframe.src = contents.shareContents.url;
});

});
