<template>
  <div id="app" :style="style">
    <Login v-if="!logged" />
    <router-view v-else />
  </div>
</template>

<script>
import Login from "./views/Login.vue";
export default {
  components: {
    Login
  },
  computed: {
    logged() {
      return this.$stiller.firebase.auth.logged;
    },
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
#app {
  background-color: darkolivegreen;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
</style>
