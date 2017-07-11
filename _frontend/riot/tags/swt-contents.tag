<swt-contents>
  <div class="sg-contents {sg-contents-separate: isDetail}">
    <swt-cover if={!isDetail && !sawitter.isLogin}></swt-cover>
    <swt-tweet if={!isDetail && sawitter.isLogin}></swt-tweet>
    <swt-timeline if={!isDetail}></swt-timeline>
    <swt-detail if={isDetail}></swt-detail>
    <swt-iframe if={isDetail}></swt-iframe>
    <swt-modal if={isShowModal}></swt-modal>
  </div>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var sawitter: any;

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    this.isDetail = false;
    this.isShowModal = false;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    sawitter.obs.on("showDetail", () => {
      this.isDetail = true;
      this.update();
    });

    sawitter.obs.on("hideDetail", () => {
      this.isDetail = false;
      this.update();
    });

    sawitter.obs.on("showModal", () => {
      this.isShowModal = true;
      this.update();
    });

    sawitter.obs.on("hideModal", () => {
      setTimeout(() => {
        this.isShowModal = false;
        this.update();
      }, 300);
    });

  </script>
</swt-contents>
