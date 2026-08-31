package com.opsat.subscribity.presentation.addsubscription

import androidx.annotation.DrawableRes
import com.opsat.subscribity.R

data class SimpleIconOption(val slug: String, val title: String, @DrawableRes val drawableResId: Int)

/**
 * A curated subset of [Simple Icons](https://simpleicons.org) — services realistically named in a
 * subscription tracker (streaming, music, cloud, productivity, gaming, social, VPN, shopping, big
 * tech) — rather than the full ~3200-icon set, so autocomplete stays relevant. Extend by generating
 * more `res/drawable/ic_brand_<slug>.xml` entries through the same pipeline (fetch the SVG path and
 * hex color for a slug from https://cdn.jsdelivr.net/npm/simple-icons@latest/, bake the color into
 * `android:fillColor`) and adding a matching row here.
 */
object SimpleIconsCatalog {
    val allIcons: List<SimpleIconOption> by lazy {
        listOf(
            SimpleIconOption(slug = "netflix", title = "Netflix", drawableResId = R.drawable.ic_brand_netflix),
            SimpleIconOption(slug = "youtube", title = "YouTube", drawableResId = R.drawable.ic_brand_youtube),
            SimpleIconOption(slug = "appletv", title = "Apple TV", drawableResId = R.drawable.ic_brand_appletv),
            SimpleIconOption(slug = "crunchyroll", title = "Crunchyroll", drawableResId = R.drawable.ic_brand_crunchyroll),
            SimpleIconOption(slug = "max", title = "Max", drawableResId = R.drawable.ic_brand_max),
            SimpleIconOption(slug = "spotify", title = "Spotify", drawableResId = R.drawable.ic_brand_spotify),
            SimpleIconOption(slug = "applemusic", title = "Apple Music", drawableResId = R.drawable.ic_brand_applemusic),
            SimpleIconOption(slug = "youtubemusic", title = "YouTube Music", drawableResId = R.drawable.ic_brand_youtubemusic),
            SimpleIconOption(slug = "soundcloud", title = "SoundCloud", drawableResId = R.drawable.ic_brand_soundcloud),
            SimpleIconOption(slug = "tidal", title = "TIDAL", drawableResId = R.drawable.ic_brand_tidal),
            SimpleIconOption(slug = "audible", title = "Audible", drawableResId = R.drawable.ic_brand_audible),
            SimpleIconOption(slug = "deezer", title = "Deezer", drawableResId = R.drawable.ic_brand_deezer),
            SimpleIconOption(slug = "googledrive", title = "Google Drive", drawableResId = R.drawable.ic_brand_googledrive),
            SimpleIconOption(slug = "dropbox", title = "Dropbox", drawableResId = R.drawable.ic_brand_dropbox),
            SimpleIconOption(slug = "icloud", title = "iCloud", drawableResId = R.drawable.ic_brand_icloud),
            SimpleIconOption(slug = "mega", title = "MEGA", drawableResId = R.drawable.ic_brand_mega),
            SimpleIconOption(slug = "notion", title = "Notion", drawableResId = R.drawable.ic_brand_notion),
            SimpleIconOption(slug = "trello", title = "Trello", drawableResId = R.drawable.ic_brand_trello),
            SimpleIconOption(slug = "asana", title = "Asana", drawableResId = R.drawable.ic_brand_asana),
            SimpleIconOption(slug = "evernote", title = "Evernote", drawableResId = R.drawable.ic_brand_evernote),
            SimpleIconOption(slug = "figma", title = "Figma", drawableResId = R.drawable.ic_brand_figma),
            SimpleIconOption(slug = "zoom", title = "Zoom", drawableResId = R.drawable.ic_brand_zoom),
            SimpleIconOption(slug = "googlemeet", title = "Google Meet", drawableResId = R.drawable.ic_brand_googlemeet),
            SimpleIconOption(slug = "todoist", title = "Todoist", drawableResId = R.drawable.ic_brand_todoist),
            SimpleIconOption(slug = "clickup", title = "ClickUp", drawableResId = R.drawable.ic_brand_clickup),
            SimpleIconOption(slug = "airtable", title = "Airtable", drawableResId = R.drawable.ic_brand_airtable),
            SimpleIconOption(slug = "googlecalendar", title = "Google Calendar", drawableResId = R.drawable.ic_brand_googlecalendar),
            SimpleIconOption(slug = "steam", title = "Steam", drawableResId = R.drawable.ic_brand_steam),
            SimpleIconOption(slug = "playstation", title = "PlayStation", drawableResId = R.drawable.ic_brand_playstation),
            SimpleIconOption(slug = "epicgames", title = "Epic Games", drawableResId = R.drawable.ic_brand_epicgames),
            SimpleIconOption(slug = "discord", title = "Discord", drawableResId = R.drawable.ic_brand_discord),
            SimpleIconOption(slug = "twitch", title = "Twitch", drawableResId = R.drawable.ic_brand_twitch),
            SimpleIconOption(slug = "ea", title = "EA", drawableResId = R.drawable.ic_brand_ea),
            SimpleIconOption(slug = "ubisoft", title = "Ubisoft", drawableResId = R.drawable.ic_brand_ubisoft),
            SimpleIconOption(slug = "x", title = "X", drawableResId = R.drawable.ic_brand_x),
            SimpleIconOption(slug = "instagram", title = "Instagram", drawableResId = R.drawable.ic_brand_instagram),
            SimpleIconOption(slug = "facebook", title = "Facebook", drawableResId = R.drawable.ic_brand_facebook),
            SimpleIconOption(slug = "tiktok", title = "TikTok", drawableResId = R.drawable.ic_brand_tiktok),
            SimpleIconOption(slug = "reddit", title = "Reddit", drawableResId = R.drawable.ic_brand_reddit),
            SimpleIconOption(slug = "telegram", title = "Telegram", drawableResId = R.drawable.ic_brand_telegram),
            SimpleIconOption(slug = "whatsapp", title = "WhatsApp", drawableResId = R.drawable.ic_brand_whatsapp),
            SimpleIconOption(slug = "snapchat", title = "Snapchat", drawableResId = R.drawable.ic_brand_snapchat),
            SimpleIconOption(slug = "pinterest", title = "Pinterest", drawableResId = R.drawable.ic_brand_pinterest),
            SimpleIconOption(slug = "threads", title = "Threads", drawableResId = R.drawable.ic_brand_threads),
            SimpleIconOption(slug = "nordvpn", title = "NordVPN", drawableResId = R.drawable.ic_brand_nordvpn),
            SimpleIconOption(slug = "expressvpn", title = "ExpressVPN", drawableResId = R.drawable.ic_brand_expressvpn),
            SimpleIconOption(slug = "protonvpn", title = "Proton VPN", drawableResId = R.drawable.ic_brand_protonvpn),
            SimpleIconOption(slug = "protonmail", title = "Proton Mail", drawableResId = R.drawable.ic_brand_protonmail),
            SimpleIconOption(slug = "1password", title = "1Password", drawableResId = R.drawable.ic_brand_1password),
            SimpleIconOption(slug = "bitwarden", title = "Bitwarden", drawableResId = R.drawable.ic_brand_bitwarden),
            SimpleIconOption(slug = "lastpass", title = "LastPass", drawableResId = R.drawable.ic_brand_lastpass),
            SimpleIconOption(slug = "surfshark", title = "Surfshark", drawableResId = R.drawable.ic_brand_surfshark),
            SimpleIconOption(slug = "ebay", title = "eBay", drawableResId = R.drawable.ic_brand_ebay),
            SimpleIconOption(slug = "uber", title = "Uber", drawableResId = R.drawable.ic_brand_uber),
            SimpleIconOption(slug = "ubereats", title = "Uber Eats", drawableResId = R.drawable.ic_brand_ubereats),
            SimpleIconOption(slug = "doordash", title = "DoorDash", drawableResId = R.drawable.ic_brand_doordash),
            SimpleIconOption(slug = "aliexpress", title = "AliExpress", drawableResId = R.drawable.ic_brand_aliexpress),
            SimpleIconOption(slug = "shopify", title = "Shopify", drawableResId = R.drawable.ic_brand_shopify),
            SimpleIconOption(slug = "etsy", title = "Etsy", drawableResId = R.drawable.ic_brand_etsy),
            SimpleIconOption(slug = "paypal", title = "PayPal", drawableResId = R.drawable.ic_brand_paypal),
            SimpleIconOption(slug = "google", title = "Google", drawableResId = R.drawable.ic_brand_google),
            SimpleIconOption(slug = "apple", title = "Apple", drawableResId = R.drawable.ic_brand_apple),
            SimpleIconOption(slug = "github", title = "GitHub", drawableResId = R.drawable.ic_brand_github),
            SimpleIconOption(slug = "gitlab", title = "GitLab", drawableResId = R.drawable.ic_brand_gitlab),
            SimpleIconOption(slug = "samsung", title = "Samsung", drawableResId = R.drawable.ic_brand_samsung),
            SimpleIconOption(slug = "meta", title = "Meta", drawableResId = R.drawable.ic_brand_meta),
            SimpleIconOption(slug = "strava", title = "Strava", drawableResId = R.drawable.ic_brand_strava),
            SimpleIconOption(slug = "fitbit", title = "Fitbit", drawableResId = R.drawable.ic_brand_fitbit),
            SimpleIconOption(slug = "headspace", title = "Headspace", drawableResId = R.drawable.ic_brand_headspace),
            SimpleIconOption(slug = "duolingo", title = "Duolingo", drawableResId = R.drawable.ic_brand_duolingo),
            SimpleIconOption(slug = "coursera", title = "Coursera", drawableResId = R.drawable.ic_brand_coursera),
            SimpleIconOption(slug = "udemy", title = "Udemy", drawableResId = R.drawable.ic_brand_udemy),
            SimpleIconOption(slug = "skillshare", title = "Skillshare", drawableResId = R.drawable.ic_brand_skillshare),
            SimpleIconOption(slug = "patreon", title = "Patreon", drawableResId = R.drawable.ic_brand_patreon),
            SimpleIconOption(slug = "onlyfans", title = "OnlyFans", drawableResId = R.drawable.ic_brand_onlyfans),
            SimpleIconOption(slug = "kickstarter", title = "Kickstarter", drawableResId = R.drawable.ic_brand_kickstarter),
            SimpleIconOption(slug = "mailchimp", title = "MailChimp", drawableResId = R.drawable.ic_brand_mailchimp),
            SimpleIconOption(slug = "wix", title = "Wix", drawableResId = R.drawable.ic_brand_wix),
            SimpleIconOption(slug = "squarespace", title = "Squarespace", drawableResId = R.drawable.ic_brand_squarespace),
            SimpleIconOption(slug = "wordpress", title = "WordPress", drawableResId = R.drawable.ic_brand_wordpress),
            SimpleIconOption(slug = "namecheap", title = "Namecheap", drawableResId = R.drawable.ic_brand_namecheap),
            SimpleIconOption(slug = "godaddy", title = "GoDaddy", drawableResId = R.drawable.ic_brand_godaddy),
            SimpleIconOption(slug = "cloudflare", title = "Cloudflare", drawableResId = R.drawable.ic_brand_cloudflare),
            SimpleIconOption(slug = "digitalocean", title = "DigitalOcean", drawableResId = R.drawable.ic_brand_digitalocean),
            SimpleIconOption(slug = "vercel", title = "Vercel", drawableResId = R.drawable.ic_brand_vercel),
            SimpleIconOption(slug = "netlify", title = "Netlify", drawableResId = R.drawable.ic_brand_netlify),
        )
    }

    fun filterIconOptions(query: String): List<SimpleIconOption> {
        if (query.isBlank()) return allIcons
        return allIcons.filter {
            it.title.contains(query, ignoreCase = true) || it.slug.contains(query, ignoreCase = true)
        }
    }

    fun exactMatchOrNull(query: String): SimpleIconOption? =
        allIcons.firstOrNull { it.title.equals(query, ignoreCase = true) || it.slug.equals(query, ignoreCase = true) }

    fun drawableResFor(slug: String): Int? = allIcons.firstOrNull { it.slug == slug }?.drawableResId
}
