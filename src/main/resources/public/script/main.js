$(function () {

    var blurbQueues = {};
    var streamDiv = $('#stream');
    var currentUser = "Aaron Krimensky";
    stream();
    $('#map').locationpicker();

    $('#relocate').click(function() {
        "use strict";
        $('#map').toggleClass('active');
    });

    function stream() {
        $.post("/stream")
            .done(function (obj) {
                obj.updates.forEach(function (update) {
                    if (!blurbQueues[currentUser]) {
                        blurbQueues[currentUser] = [];
                    }
                    blurbQueues[currentUser].push(update);
                });
                stream();
            })
            .fail(function () {
                setTimeout(stream, 2000);
            });
    }

    setInterval(function () {
        if (!blurbQueues[currentUser] || blurbQueues[currentUser].length == 0) {
            return;
        }
        var update = blurbQueues[currentUser].shift();
        var blurb;
        if (update.type == "like") {
            blurb = likeBlurb(update);
        }
        streamDiv.append(blurb);
        setTimeout(function () {
            blurb.removeClass('initial');
        }, 50);
    }, 500);

    function blurb(type, text, image) {
        var outerDiv = $('<div class="initial ' + type + ' blurb">');
        var innerDiv = $('<div class="blurb-table">');
        var blurbText = $('<div class="blurb-text">');
        blurbText.text(text);
        innerDiv.append(blurbText);
        var img = $('<img src="' + image + '">');
        innerDiv.append(img);
        outerDiv.html(innerDiv);
        return outerDiv;
    }

    function likeBlurb(update) {
        return blurb('like', 'Liked ' + update.name, update.image);
    }
})