webpackJsonp([2],{"7zck":function(e,n){},A3zz:function(e,n,r){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var t=r("7+uW"),i=r("3EgV"),a=r.n(i),s={render:function(){var e=this,n=e.$createElement,r=e._self._c||n;return r("div",{attrs:{id:"login"}},[r("v-app",{attrs:{id:"inspire"}},[r("v-content",[r("v-container",{attrs:{fluid:"","fill-height":""}},[r("v-layout",{attrs:{"align-center":"","justify-center":""}},[r("v-flex",{attrs:{xs12:"",sm8:"",md4:""}},[r("v-card",{staticClass:"elevation-12"},[r("v-toolbar",{attrs:{dark:"",color:"primary"}},[r("v-toolbar-title",[e._v(e._s(e.i18n.title_login))])],1),e._v(" "),r("v-card-text",[r("v-form",{ref:"form",attrs:{method:"POST",action:"j_security_check","lazy-validation":""},model:{value:e.valid,callback:function(n){e.valid=n},expression:"valid"}},[r("v-text-field",{attrs:{"prepend-icon":"person",name:"j_username",label:e.i18n.label_username,type:"text",rules:[function(){return e.username.length>0||e.i18n.rule_required}],required:""},model:{value:e.username,callback:function(n){e.username=n},expression:"username"}}),e._v(" "),r("v-text-field",{attrs:{"prepend-icon":"lock",name:"j_password",label:e.i18n.label_password,id:"password",type:"password",rules:[function(){return e.password.length>0||e.i18n.rule_required}],required:""},model:{value:e.password,callback:function(n){e.password=n},expression:"password"}})],1)],1),e._v(" "),r("v-card-actions",[r("v-spacer"),e._v(" "),r("v-btn",{attrs:{type:"submit",color:"primary"},on:{click:e.submit}},[e._v(e._s(e.i18n.btn_login))]),e._v(" "),r("v-spacer")],1),e._v(" "),r("v-card-text",[e._v("\n                "+e._s(e.i18n.label_register)+"\n                "),r("a",{attrs:{href:"/login/signup"}},[e._v(e._s(e.i18n.btn_signup))])])],1)],1)],1)],1)],1)],1)],1)},staticRenderFns:[]};var l=r("VU/8")({name:"Login",data:function(){return{username:"",password:"",valid:!0,i18n:{title_login:"Login form",label_username:"Email",label_password:"Password",label_register:"New user?",btn_login:"Login",btn_signup:"Sign up",rule_required:"This field is required"}}},methods:{submit:function(){this.$refs.form.validate()&&this.$refs.form.$el.submit()}}},s,!1,function(e){r("oGRF")},null,null).exports;r("7zck");t.a.config.productionTip=!1,t.a.use(a.a),new t.a({el:"#login",components:{Login:l},template:"<Login/>"})},oGRF:function(e,n){}},["A3zz"]);
//# sourceMappingURL=login.js.map