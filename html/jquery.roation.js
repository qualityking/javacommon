(function ( $ ) {
  $.fn.rotate = function( config, callback ) {
    // get $ this ref
    var $this = $(this);
	$this.css('display','block');
	
    // define defaults opts
    var opts = {
      degrees: 360,
      speed: 100,
	  start:0,
	  moveto:100
    };
	var move;
    // extend config
    $.extend( opts, config );

    // perform animation
    $({deg: 0}).animate({deg: opts.degrees}, {
      duration: opts.speed,
      step: function ( now ) {
		if(now<(opts.moveto - opts.start)){
			move = now;
		}
        $this.css({
          transform: "rotate(" + now + "deg)",
          "-webkit-transform": "rotate(" + now + "deg)",
          "-ms-transform": "rotate(" + now + "deg)",
          "-moz-transform": "rotate(" + now + "deg)",
          "-o-transform": "rotate(" + now + "deg)"
        }).css("left",move+opts.start);
      },
      complete: function () {
        // check for callback
        // and if so append it to
        // animate complete
        if (typeof callback == 'function') callback.call(this);
      }
    });

    // return this for method chaining
    return this;
  };

} ( jQuery ) );
