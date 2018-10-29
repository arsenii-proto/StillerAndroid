<template>
    <div class="form-buttons">

        <div class="button" @click="clicked">1</div>
        <div class="button" @click="clicked">2</div>
        <div class="button" @click="clicked">3</div>

        <div class="button" @click="clicked">4</div>
        <div class="button" @click="clicked">5</div>
        <div class="button" @click="clicked">6</div>

        <div class="button" @click="clicked">7</div>
        <div class="button" @click="clicked">8</div>
        <div class="button" @click="clicked">9</div>

        <div class="button zero" @click="clicked">0</div>
        <div class="button" @click="clicked">C</div>

    </div>
</template>

<script>
import { mapActions } from "vuex";

export default {
  name: "login-form-buttons",
  methods: {
    ...mapActions({
      clicked: "loginState/numberChanged"
    })
  }
};
</script>

<style>
.form-buttons {
  display: flex;
  padding: 1rem 2rem;
  flex-wrap: wrap;
  justify-content: space-between;
}
.form-buttons .button {
  width: 26%;
  box-sizing: border-box;
  margin: 0.6rem 0;
  background: rgba(0, 0, 0, 0.6);
  padding: 1rem 1.5rem;
  border-radius: 0.5rem;
  font-size: 1.5rem;
  text-align: center;
  text-shadow: 0px 0px 2px #333;
}
.form-buttons .button.zero {
  width: 63%;
}
</style>
