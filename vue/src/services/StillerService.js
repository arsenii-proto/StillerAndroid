function Stiller(stiller) {
  const self = this;

  const promises = {};
  const binders = {};

  const prepareProps = (obj, config) => {
    // console.log(config);
    if (typeof config !== "object" && typeof config === "string") {
      try {
        config = JSON.parse(config);
      } catch (e) {
        throw new Error(
          "StillerBridge config must be type of object or object json string representation"
        );
      }
    }

    if (typeof config !== "object") {
      throw new Error(
        "StillerBridge config must be type of object or object json string representation"
      );
    }

    if ("value" in config && Object.keys(config).length == 1) {
      return config.value;
    }

    for (const name in config) {
      const value = config[name];
      let type = "simple";
      let id = null;

      if (typeof value === "string") {
        const regex = /^@(prop|method|binder)\(([A-z0-9]+)\)$/;
        const match = regex.exec(value);

        if (match && match.length == 3) {
          type = match[1];
          id = match[2];
        }
      }

      Object.defineProperty(obj, name, propProxy(type, value, id));
    }

    return obj;
  };

  const propProxy = (type, value, id) => {
    if (type === "prop") {
      return {
        enumerable: true,
        configurable: true,

        get() {
          return prepareProps({}, stiller.getProp(id));
        },

        set(newValue) {
          if (typeof newValue !== "object") {
            newValue = { value: newValue };
          }

          stiller.setProp(id, JSON.stringify(newValue));
        }
      };
    }

    if (type === "method") {
      const call = arg => {
        const value = typeof arg !== "object" ? { value: arg } : arg;
        const response = JSON.parse(
          stiller.callMethod(id, JSON.stringify(value))
        );
        const proxyMatch = /^@promise\(([A-z0-9]+)\)$/.exec(
          response.result || ""
        );

        if (proxyMatch && proxyMatch.length == 2) {
          const id = proxyMatch[1];

          return new Promise((t, c) => {
            promises[id] = {
              resolve: t,
              reject: c
            };
          });
        }

        if ("result" in response && Object.keys(response).length == 1) {
          if (
            /^@(prop|method|binder)\(([A-z0-9]+)\)$/.exec(response.result || "")
          ) {
            return prepareProps({}, response.result);
          }

          return response.result;
        }

        return prepareProps({}, response);
      };

      return {
        configurable: false,

        get() {
          return arg => call(arg || {});
        }
      };
    }

    if (type === "binder") {
      if (id in binders) {
        return {
          value: binders[id].value,
          configurable: false
        };
      }

      const binder = {
        value: {},

        listeners: [],

        on(fun) {
          this.listeners.push(fun instanceof Function ? fun : () => {});

          if (this.listeners.length) {
            stiller.startBinder(id);
          }
        },

        off(fun) {
          this.listeners.forEach((_fun, i) => {
            if (_fun === fun) {
              this.listeners.splice(i, 1);
            }
          });

          if (!this.listeners.length) {
            stiller.stopBinder(id);
          }
        },

        dispatch(arg) {
          if (!this.listeners) return;

          this.listeners.forEach(fun => fun(arg));
        }
      };

      binders[id] = binder;

      Object.defineProperties(binder.value, {
        on: {
          configurable: false,
          get() {
            return (t, f) => binder.on(t, f);
          }
        },

        off: {
          configurable: false,
          get() {
            return (t, f) => binder.off(t, f);
          }
        }
      });

      return {
        value: binder.value,
        configurable: false
      };
    }

    return {
      value: value,
      writable: true,
      enumerable: false,
      configurable: false
    };
  };

  prepareProps(self, stiller.initial());

  stiller.resolvePromise = (id, arg) => {
    if (id in promises) {
      promises[id].resolve(prepareProps({}, arg));
    }
  };

  stiller.rejectPromise = (id, arg) => {
    if (id in promises) {
      promises[id].reject(prepareProps({}, arg));
    }
  };

  stiller.dispatchEvent = (id, arg) => {
    if (id in binders) {
      binders[id].dispatch(prepareProps({}, arg));
    }
  };
}

let service;

if ("stiller" in window) {
  if (
    window.stiller.initial &&
    window.stiller.getProp &&
    window.stiller.setProp &&
    window.stiller.callMethod
  ) {
    service = new Stiller(window.stiller);
  }
}

export default {
  install: function(Vue) {
    return (Vue.prototype.$stiller = service);
  }
};

export { service as Stiller };
