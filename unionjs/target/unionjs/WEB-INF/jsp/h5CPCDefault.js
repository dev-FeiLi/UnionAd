!(function () {
    var _ua = navigator.userAgent.toLowerCase(), _elem = "div",N=navigator.platform, doc = document, win = window, D = screen, a = {
        A: function (css) {
            if (css == undefined) return false;
            var h = doc.getElementsByTagName('head')[0];
            var s = doc.createElement('style');
            s.appendChild(doc.createTextNode(css));
            h.appendChild(s);
        },
        B: function (con) {
            if (con == undefined) return false;
            doc.write(con);
        },
        C: function (g) {
            setTimeout(function () {
                var xhr = new XMLHttpRequest();
                xhr.open('get', g, true);
                xhr.send();
            }, 1000);
        },
        D: function (s) {
            var sp = doc.createElement("script");
            sp.src = s;
            doc.body.appendChild(sp);
        },
        E: function () {
            var list = doc.getElementsByClassName("${idname}Gb");
            if (list.length > 0) {
                list.item(0).addEventListener('click', function (e) {
                    var div = doc.getElementById('${idname}Wc');
                    div.style.display = 'none';
                });
            }
        }
    };
    ;
    var Base64 = {
        k: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=", encode: function (input) {
            var output = "";
            var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
            var i = 0;
            input = Base64.B(input);
            while (i < input.length) {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);
                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;
                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }
                output = output + Base64.k.charAt(enc1) + Base64.k.charAt(enc2) + Base64.k.charAt(enc3) + Base64.k.charAt(enc4);
            }
            return output;
        }, B: function (string) {
            string = string.replace(/\r\n/g, "\n");
            var utftext = "";
            for (var n = 0; n < string.length; n++) {
                var c = string.charCodeAt(n);
                if (c < 128) {
                    utftext += String.fromCharCode(c);
                } else if ((c > 127) && (c < 2048)) {
                    utftext += String.fromCharCode((c >> 6) | 192);
                    utftext += String.fromCharCode((c & 63) | 128);
                } else {
                    utftext += String.fromCharCode((c >> 12) | 224);
                    utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                    utftext += String.fromCharCode((c & 63) | 128);
                }
            }
            return utftext;
        }
    };

    function __E(a, f) {
        if (a.length && a.slice) {
            for (i = 0; i < a.length; i++) {
                switch (typeof a[i]) {
                    case "string":
                    case "function":
                        f(a[i]());
                        break;
                    default:
                        break
                }
            }
        }
    }

    function __M(o, t) {
        if (!o || !t) {
            return o
        }
        for (var tem in t) {
            o[tem] = t[tem];
        }
        return o;
    }

    function __P() {
        var p = [];

        function r() {
            var c;
            try {
                c = win.opener ? win.opener.document.location.href : doc.referrer
            } catch (e) {
                c = doc.referrer
            }
            c = {r: encodeURIComponent(c)};
            return c;
        }

        function u() {
            var c;
            try {
                c = win.top.document.location.href;
            } catch (e) {
                c = doc.location.href;
            }
            return {u: encodeURIComponent(c)};
        }

        function j() {
            return {j: navigator.javaEnabled() ? 1 : 0};
        }

        function p() {
            return {p: navigator.plugins.length};
        }

        function m() {
            return {m: navigator.mimeTypes.length};
        }

        function f() {
            var v = 0;
            if (navigator.plugins && navigator.mimeTypes.length) {
                var b = navigator.plugins["Shockwave Flash"];
                if (b && b.description) v = b.description.replace(/([a-zA-Z]|\s)+/, "").replace(/(\s)+r/, ".")
            }
            return {f: v}
        }

        function s() {
            var v = D.width + "x" + D.height;
            return {s: v}
        }

        function c() {
            var v = navigator.cookieEnabled;
            v = v ? 1 : 0;
            return {c: v}
        }

        function H() {
            return document.body && {h: document.body.clientHeight};
        }

        function t() {
            return {t: encodeURIComponent(doc.title)};
        }

        var b = {};
        __E([j, p, m, f, r, u, s, c, H, t], function (a) {
            __M(b, a)
        });
        for (var e in b) {
            p.push(e + "=" + b[e]);
        }
        return Base64.encode(p.join("&"));
    }

    function __C(e) {
        var u = this.src;
        try {
            var x = e.originalEvent.targetTouches[0].clientX, y = e.originalEvent.targetTouches[0].clientY;
            this.src = u + "&pos=" + x + "," + y;
        } catch (e) {
        }
    }
    //var plf=N.platform.toLowerCase();if (plf.indexOf("win")>-1||plf.indexOf("mac")>-1)return;
    //<c:if test='${isScriptUrl}'>a.D('${imageurl}');</c:if>
    //<c:if test='${!isScriptUrl}'>
    var url = '${clickurl}&' + __P();
    var _c = ".${idname}Wc {position:relative} .#${idname}He img{width:100%;}";
    var _b = "<div class='${idname}Wc'><a href='" + url + "' target='_blank' id='${idname}He'><img src='${imageurl}' border='0' alt=''/></a></div>";
    a.A(_c);
    a.B(_b);
    doc.getElementById('${idname}He').onclick = __C;
    //</c:if>
    a.C('${counturl}&' + __P());//<c:if test='ispop'>
    win.location.href=url;// </c:if>
})();
