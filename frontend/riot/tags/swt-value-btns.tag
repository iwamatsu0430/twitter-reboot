<swt-value-btns>
  <div class="sg-contents-timeline-btn">
    <div if={sawitter.isLogin && !opts.value.isValued}>
      <a class="sg-contents-timeline-btn-good" onclick={onPutGood} href="#">
        <i class="fa fa-thumbs-up"></i> {opts.value.good}
        <span>
          <i class="fa fa-thumbs-up"></i> Good
        </span>
      </a>
    </div>
    <div if={sawitter.isLogin && !opts.value.isValued}>
      <a class="sg-contents-timeline-btn-bad" onclick={onPutBad} href="#">
        <i class="fa fa-thumbs-down"></i> {opts.value.bad}
        <span>
          <i class="fa fa-thumbs-down"></i> Bad
        </span>
      </a>
    </div>

    <div if={sawitter.isLogin && opts.value.isValued}>
      <a class="sg-contents-timeline-btn-good sg-contents-timeline-btn-complete" onclick={onCancel} href="#">
        <i class="fa fa-thumbs-up"></i> {opts.value.good}
        <span>Cancel</span>
      </a>
    </div>
    <div if={sawitter.isLogin && opts.value.isValued}>
      <a class="sg-contents-timeline-btn-bad sg-contents-timeline-btn-complete" onclick={onCancel} href="#">
        <i class="fa fa-thumbs-down"></i> {opts.value.bad}
        <span>Cancel</span>
      </a>
    </div>

    <div if={!sawitter.isLogin}>
      <a class="sg-contents-timeline-btn-good">
        <i class="fa fa-thumbs-up"></i> {opts.value.good}
      </a>
    </div>
    <div if={!sawitter.isLogin}>
      <a class="sg-contents-timeline-btn-bad">
        <i class="fa fa-thumbs-down"></i> {opts.value.bad}
      </a>
    </div>
  </div>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var opts: any;
    declare var sawitter: any;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    this.onPutGood = e => {
      e.preventDefault();
      sawitter.putGood(opts.tweetid);
    };

    this.onPutBad = e => {
      e.preventDefault();
      sawitter.putBad(opts.tweetid);
    };

    this.onCancel = e => {
      e.preventDefault();
      var result = confirm("評価を取り消しますか？");
      if (result) {
        sawitter.putCancel(opts.tweetid);
      }
    };
  </script>
</swt-value-btns>
