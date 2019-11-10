/**
 * Configuration for head elements added during the creation of index.html.
 *
 * All href attributes are added the publicPath (if exists) by default.
 * You can explicitly hint to prefix a publicPath by setting a boolean value to a key that has
 * the same name as the attribute you want to operate on, but prefix with =
 *
 * Example:
 * { name: 'msapplication-TileImage', content: '/assets/favicon/ms-icon-144x144.png', '=content': true },
 * Will prefix the publicPath to content.
 *
 * { rel: 'apple-touch-icon', sizes: '57x57', href: '/assets/favicon/apple-icon-57x57.png', '=href': false },
 * Will not prefix the publicPath on href (href attributes are added by default
 *
 */
module.exports = {
  link: [
    /**
     * <link> tags for 'apple-touch-icon' (AKA Web Clips).
     */
    { rel: 'apple-touch-icon', sizes: '57x57', href: '/assets/favicon/apple-touch-icon-57x57.png' },
    { rel: 'apple-touch-icon', sizes: '60x60', href: '/assets/favicon/apple-touch-icon-60x60.png' },
    { rel: 'apple-touch-icon', sizes: '72x72', href: '/assets/favicon/apple-touch-icon-72x72.png' },
    { rel: 'apple-touch-icon', sizes: '76x76', href: '/assets/favicon/apple-touch-icon-76x76.png' },
    { rel: 'apple-touch-icon', sizes: '114x114', href: '/assets/favicon/apple-touch-icon-114x114.png' },
    { rel: 'apple-touch-icon', sizes: '120x120', href: '/assets/favicon/apple-touch-icon-120x120.png' },
    { rel: 'apple-touch-icon', sizes: '144x144', href: '/assets/favicon/apple-touch-icon-144x144.png' },
    { rel: 'apple-touch-icon', sizes: '152x152', href: '/assets/favicon/apple-touch-icon-152x152.png' },
    { rel: 'apple-touch-icon', sizes: '180x180', href: '/assets/favicon/apple-touch-icon-180x180.png' },

    /**
     * <link> tags for android web app icons
     */
    { rel: 'icon', type: 'image/png', sizes: '384x384', href: '/assets/favicon/android-chrome-384x384.png' },
    { rel: 'icon', type: 'image/png', sizes: '256x256', href: '/assets/favicon/android-chrome-256x256.png' },
    { rel: 'icon', type: 'image/png', sizes: '192x192', href: '/assets/favicon/android-chrome-192x192.png' },
    { rel: 'icon', type: 'image/png', sizes: '144x144', href: '/assets/favicon/android-chrome-144x144.png' },
    { rel: 'icon', type: 'image/png', sizes: '96x96', href: '/assets/favicon/android-chrome-96x96.png' },
    { rel: 'icon', type: 'image/png', sizes: '72x72', href: '/assets/favicon/android-chrome-72x72.png' },
    { rel: 'icon', type: 'image/png', sizes: '48x48', href: '/assets/favicon/android-chrome-48x48.png' },
    { rel: 'icon', type: 'image/png', sizes: '36x36', href: '/assets/favicon/android-chrome-36x36.png' },

    /**
     * <link> tags for favicons
     */
    { rel: 'icon', type: 'image/png', sizes: '16x16', href: '/assets/favicon/favicon-16x16.png' },
    { rel: 'icon', type: 'image/png', sizes: '32x32', href: '/assets/favicon/favicon-32x32.png' },

    /**
     * <link> tags for a Web App Manifest
     */
    { rel: 'manifest', href: '/assets/manifest.json' }
  ],
  meta: [
    { name: 'msapplication-TileColor', content: '#ffffff' },
    { name: 'msapplication-TileImage', content: '/assets/favicon/mstile-144x144.png', '=content': true },
    { name: 'theme-color', content: '#ffffff' }
  ]
};
