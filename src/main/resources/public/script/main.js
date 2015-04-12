$(function () {

    var blurbQueues = {};
    var blurbStreams = {};
    var streamDiv = $('#stream');
    var bots = {};
    var currentBot;

    $('#chatPopup').css('display', 'none');
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

        $('.matchtab').each(function() {

            if($(this).attr('id') == 'tab_' + bots[currentBot].name) {
                $(this).css('display', '');
                console.log('show');
            }
            else
                $(this).css('display', 'none');
        });
    });

    $('#exitButton').click(function() {
        $('#chatPopup').css('display', 'none');
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

            for (botId in bots) {
                var bot = bots[botId];

                $('#content').append('<div class="matchtab" id="tab_' + bot.name + '">');

                for(var i=0; i < bot.matchedUsers.length; i++) {
                    photo = getMainPhoto(bot.matchedUsers[i].photos);

                    if(photo != undefined)
                        $('#tab_'+bot.name).append('<div userIndex="' + i + '" class="matchdiv"><img src="' + photo.url84 + '"></img><h3>' + bot.matchedUsers[i].name + ' ' +  bots[currentBot].matchedUsers[i].messages.length + '</h3></div>');
                }

                $('#content').append('</div>');

                if(currentBot != botId) {
                    console.log(bot.name);
                    $('#tab_' + bot.name).css('display', 'none');
                }
            }

            $('.matchdiv').click(function() {
                var userIndex = $(this).attr('userIndex');

                $('#chatPopup').css('display', '');
                $('#popupPhotos').empty();
                $('#textWindow').empty();

                var user = bots[currentBot].matchedUsers[userIndex];

                $('#popupPhotos').append('<div id="subPhotos">');
                for(var i=0; i < user.photos.length; i++) {
                    $('#subPhotos').append('<img class="popupImg" src="' + user.photos[i].url172 + '"></img>');
                }

                for(var i=0; i < user.messages.length; i++) {
                    if(user.messages[i].from == user.id) {
                        // allign left
                        $('#textWindow').append('<div class="left">' + user.messages[i].text + '</div>');
                    }
                    else {
                        // allign right
                        $('#textWindow').append('<div class="right">' + user.messages[i].text + '</div>');
                    }
                }
            });
        });

    function getMainPhoto(photos) {
	return (photos.filter(function(photo) {
            return photo.main;
        })[0] || photos[0]);
    }

    function stream() {
        $.post("/stream")
            .done(function (obj) {
                obj.updates.forEach(function (update) {
                    if (update.type == "location") {
                        bots[update.bot].latitude = update.latitude;
                        bots[update.bot].longitude = update.longitude;
                        remap();
                        return;
                    }
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
        } else {
            return;
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
        var mainPhoto = (update.user.photos.filter(function(photo) {
            return photo.main;
        })[0] || update.user.photos[0]);
        return blurb('match', "Matched With " + update.user.name, mainPhoto.url84);
    }

    function messageBlurb(update) {
        fromId = update.message.from;
        toId = update.message.to;

        bot = bot[0]; // not valid
        found = false;
// algorithm banks on message having vaild bot tinder id

        for(var i=0; i < bots.length; i++) {
            if(bots[i].id == toId) {
                bot = bots[i]; // bot found. flag to search for match using from id
                found = true;
                break;
            }
        }
        if(!found) {
            for(var i=0; i < bots.length; i++) {
                if(bots[i].id == fromId) {
                    bot = bots[i];
                    // bot found search for match using to id
                    for(var j=0; j < bot.matchedUsers.length; j++) {
                        if(bot.matchedUsers[j] == toId) {
                            //match found at index j of bot i
                            bot.matchedUsers[j].messages.push(update.message);
                            $('#textWindow').append('<div class="right">' + update.message.text + '</div>');
                            break;
                        }
                    }
                    break;
                }
            }
        }
        else {
            // search for match using from id
            for(var j=0; j < bot.matchedUsers.length; j++) {
                if(bot.matchedUsers[j] == fromId) {
                    // bot found at index j of bot i
                    bot.matchedUsers[j].messages.push(update.message);
                    $('#textWindow').append('<div class="left">' + update.message.text + '</div>');
                    break;
                }
            }
        }
    }
});
