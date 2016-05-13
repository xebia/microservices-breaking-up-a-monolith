#
# This is an example VCL file for Varnish.
#
# It does not do anything by default, delegating control to the
# builtin VCL. The builtin VCL is called when there is no explicit
# return statement.
#
# See the VCL chapters in the Users Guide at https://www.varnish-cache.org/docs/
# and http://varnish-cache.org/trac/wiki/VCLExamples for more examples.

# Marker to tell the VCL compiler that this VCL has been adapted to the
# new 4.0 format.
vcl 4.0;

# Default backend definition. Set this to point to your content server.
backend default {
    .host = "192.168.99.100";
    .port = "9005";
}

backend shop {
    .host = "192.168.99.100";
    .port = "9002";
}

sub vcl_recv {
  if (req.url ~ "^/shop/(.*)$") {
       set req.backend_hint = shop;
       set req.url = regsub(req.url, "^/shop/(.*)$", "/\1");
    }
}

sub vcl_backend_response {
    set beresp.do_esi = true; // Do ESI processing


}
