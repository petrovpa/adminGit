/**
 * Look in ./config folder for webpack.dev.js
 */
switch (process.env.NODE_ENV) {
  case 'prod':
  case 'production':
    module.exports = require('./config/webpack.prod')({env: 'production'});
    break;
  // тест
  case 'test':
  case 'testing':
    module.exports = require('./config/webpack.test')({env: 'test'});
    break;
  // rybinsk.bivgroup.com
  case 'biv':
    module.exports = require('./config/webpack.biv')({env: 'test'});
    break;
  // авто тестирование
  case 'karma-test':
    module.exports = require('./config/webpack.karma-test')({env: 'test'});
    break;
  case 'dev':
  case 'development':
  default:
    module.exports = require('./config/webpack.dev')({env: 'development'});
}
