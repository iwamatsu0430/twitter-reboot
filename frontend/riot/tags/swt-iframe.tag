<swt-iframe>
  <iframe class="sg-contents-iframe" name="contentsIframe"></iframe>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var sawitter: any;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    sawitter.obs.on("onContentsLoaded", contents => {
      this.contentsIframe.src = contents.shareContents.url;
    });
  </script>
</swt-iframe>
