<template>
  <div id="app" :style="style">
    <Login v-if="!logged" />
    <router-view v-else />
  </div>
</template>

<script>
import Login from "./views/Login.vue";
import { mapState } from "vuex";
export default {
  components: {
    Login
  },
  computed: {
    ...mapState({
      logged: ({ user }) => user.logged
    }),
    style() {
      return {
        "border-top": `${this.$stiller.sizes.statusBarHeight}px solid #000`,
        "border-bottom": `${this.$stiller.sizes.navBarHeight}px solid #000`
      };
    }
  }
};
</script>

<style>
@import url("https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons");
@import url("https://fonts.googleapis.com/css?family=Comfortaa|Crimson+Text|Montserrat|Nanum+Gothic|Raleway");
#app {
  background-color: rgb(18, 18, 18);
  color: rgba(255, 255, 255, 0.88);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
}
* {
  color: rgba(255, 255, 255, 0.88);
  font-family: "Comfortaa", cursive;
}
</style>
