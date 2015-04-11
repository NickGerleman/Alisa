$(function () {

    var blurbQueues = {};
    var blurbStreams = {};
    var streamDiv = $('#stream');
    var bots = {};
    var currentBot;

    stream();

    $('#relocate').click(function() {
        "use strict";
        $('#map').toggleClass('active');
    });

    $('#bot-select').change(function() {
        blurbStreams[currentBot] = streamDiv;
        currentBot = $('#bot-select').val();
        streamDiv= blurbStreams[currentBot] || $('<div id="stream">');
        $('#stream').replaceWith(streamDiv);
        remap();
    });

    $.get("/all")
        .done(function(obj) {
            obj.bots.forEach(function(bot) {
                bots[bot.id] = bot;

            });
            for (var botId in bots) {
                if (!currentBot) {
                    currentBot = botId;
                }
                $('#bot-select').append('<option value="' + botId + '">' + bots[botId].name + '</option>')
            }
            remap();
        });

    function stream() {
        $.post("/stream")
            .done(function (obj) {
                obj.updates.forEach(function (update) {
                    if (!blurbQueues[update.bot]) {
                        blurbQueues[update.bot] = [];
                    }
                    blurbQueues[update.bot].push(update);
                });
                stream();
            })
            .fail(function () {
                setTimeout(stream, 2000);
            });
    }

    function remap() {
        console.log(bots);
        console.log(currentBot);
        var domMap = $('#map');
        var map = $('<div id="map">');
        if (domMap.hasClass('active')) {
            map.addClass('active');
        }
        domMap.replaceWith(map);
        map.locationpicker({
            radius: 0,
            location: {
                latitude: bots[currentBot].latitude,
                longitude: bots[currentBot].longitude
            },
            onchanged: setLocation
        });
    }

    function setLocation(location) {
        $.ajax({
            method: 'PUT',
            url: '/' + currentBot + '/location',
            data: {latitude: location.latitude, longitude: location.longitude}
        });
        bots[currentBot].latitude = location.latitude;
        bots[currentBot].longitude = location.longitude;
    }

    setInterval(function () {
        if (!blurbQueues[currentBot] || blurbQueues[currentBot].length == 0) {
            return;
        }
        var update = blurbQueues[currentBot].shift();
        var blurb;
        if (update.type == "like") {
            blurb = likeBlurb(update);
        } else if (update.type == 'match') {
            blurb = matchBlurb(update);
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

    function matchBlurb(update) {
        var mainPhoto = update.user.photos.map(function(photo) {
            return photo.main;
        })[0];
        return blurb('match', "Matched With " + update.user.name, mainPhoto.url84);
    }
});