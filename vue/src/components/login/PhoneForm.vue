<template>
  <div class="phone-form">
    <div class="top-elements">
      <h1 class="title">
        {{ trans('login.title') }}
      </h1>
      <h3 class="small-title">
        {{ trans('login.phone.title') }}
      </h3>
      <div class="holds">
        <div class="hold country">
          {{ countryCode }}
        </div>
        <div class="hold phone-number">
          {{ phoneNumber }}
        </div>
      </div>
    </div>
    <div class="bottom-elements">
      <FormButtons />
      <div :class="{'button-actions': true, 'inactive': !phoneNumberValid}">
        {{ trans('login.phone.btn') }}
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import FormButtons from "./FormButtons.vue";

export default {
  name: "PhoneForm",
  components: { FormButtons },
  computed: {
    ...mapState({
      countryCode: ({ loginState }) => loginState.countryCode,
      phoneNumber: ({ loginState }) => loginState.phoneNumber,
      phoneNumberValid: ({ loginState }) => loginState.phoneNumberValid
    })
  }
};
</script>

<style>
.phone-form .holds .hold.country {
  flex-grow: 1;
}
.phone-form .holds .hold.phone-number {
  flex-grow: 2;
}
</style>
