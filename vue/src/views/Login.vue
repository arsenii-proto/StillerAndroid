<template>
  <div class="login">
    <transition name="fade">
      <PhoneForm class="not-faded" v-if="!codeSended"/>
      <CodeForm class="not-faded" v-else/>
    </transition>
  </div>
</template>

<script>
import PhoneForm from "@/components/login/PhoneForm.vue";
import CodeForm from "@/components/login/CodeForm.vue";
import { mapState } from "vuex";

export default {
  name: "login",
  components: {
    PhoneForm,
    CodeForm
  },
  computed: {
    ...mapState({
      codeSended: ({ loginState }) => loginState.codeSended
    })
  }
};
</script>

<style>
.login {
  background-image: url("../assets/login-background.jpg");
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  flex-grow: 1;
  position: relative;
}
.login::before {
  content: "";
  background-color: rgba(0, 0, 0, 0.9);
  background-image: url("../assets/login-background-dots.png");
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  opacity: 0.5;
}
.login .not-faded {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
  display: flex;
  flex-direction: column;
}

.login .not-faded .top-elements,
.login .not-faded .bottom-elements {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
}

.login .title {
  text-align: center;
  font-size: 2rem;
  text-shadow: 0px 0px 2px #000;
  margin: 0;
}
.login .small-title {
  text-align: center;
  font-size: 1.2rem;
  padding: 0 3rem;
  text-shadow: 0px 0px 5px #000;
  margin: 0;
}

.login .holds {
  display: flex;
  padding: 1rem 3rem;
}

.login .holds .hold {
  margin: 0 0.5rem;
  background: rgba(0, 0, 0, 0.4);
  padding: 1rem 1.5rem;
  border-radius: 0.5rem;
  font-size: 1.5rem;
  text-align: center;
  text-shadow: 0px 0px 2px #333;
}

.login .not-faded .button-actions {
  background-color: rgba(0, 0, 0, 0.9);
  margin: 0 auto;
  display: table;
  padding: 1rem 1.5rem;
  border-radius: 0.5rem;
  font-size: 1.2rem;
  color: #1de4ae;
  transition: all 0.5;
}

.login .not-faded .button-actions.inactive {
  margin-bottom: -2rem;
  opacity: 0;
}

.login .not-faded.fade-enter-active,
.login .not-faded.fade-leave-active {
  transition: all 0.5s;
}
.login .not-faded.fade-enter {
  opacity: 0;
  left: 100%;
}
.login .not-faded.fade-leave-to {
  opacity: 0;
  left: -100%;
}
</style>
