1. Things I learned
-> Android Sharesheet: a composable, which is used to share data; in other words, whenever you share applications you you encounter it
-> In **Kotlin (for Android development)**, an **Intent** is a messaging object used to request an action from another component — such as starting a new activity, opening a webpage, sending an email, etc.

   Let’s go through it **step by step**, intuitively and with examples 👇

   ---

   ## 🧩 What is an Intent?

   An **Intent** expresses an *intention* to do something.
   For example:

   * “Open the Settings app.”
   * “Start a new screen (Activity).”
   * “Send data to another Activity.”

   In code, it’s just an object that carries **action** + **data** + **optional extras**.

   ---

   ## ⚙️ Types of Intents

   ### 1. **Explicit Intents**

   Used to start a **specific Activity** or **Service** *inside your own app*.

   You explicitly name the target class.

   #### Example – Start another activity:

   ```kotlin
   // Inside MainActivity.kt
   val intent = Intent(this, SecondActivity::class.java)
   intent.putExtra("username", "Mustafa")
   startActivity(intent)
   ```

   #### Receiving data in SecondActivity:

   ```kotlin
   // Inside SecondActivity.kt
   val username = intent.getStringExtra("username")
   textView.text = "Hello, $username!"
   ```

   ---

   ### 2. **Implicit Intents**

   Used when you want the system to decide *which app* can handle your request.
   You **don’t specify a class name**, just an action (and sometimes data).

   #### Example – Open a webpage:

   ```kotlin
   val intent = Intent(Intent.ACTION_VIEW)
   intent.data = Uri.parse("https://www.google.com")
   startActivity(intent)
   ```

   #### Example – Send an email:

   ```kotlin
   val intent = Intent(Intent.ACTION_SENDTO).apply {
       data = Uri.parse("mailto:")
       putExtra(Intent.EXTRA_EMAIL, arrayOf("someone@example.com"))
       putExtra(Intent.EXTRA_SUBJECT, "Hello from Kotlin!")
   }
   startActivity(intent)
   ```

   #### Example – Share text:

   ```kotlin
   val intent = Intent(Intent.ACTION_SEND).apply {
       type = "text/plain"
       putExtra(Intent.EXTRA_TEXT, "Check out my new app!")
   }
   startActivity(Intent.createChooser(intent, "Share via"))
   ```

   ---

   ## 📦 Passing Data Between Activities

   You can attach small pieces of data (called **extras**) to your Intent.

   | Type                              | Function         |
   | --------------------------------- | ---------------- |
   | `putExtra("key", value)`          | Add data         |
   | `getStringExtra("key")`           | Retrieve String  |
   | `getIntExtra("key", default)`     | Retrieve Int     |
   | `getBooleanExtra("key", default)` | Retrieve Boolean |

   Example:

   ```kotlin
   // Sending
   val intent = Intent(this, DetailActivity::class.java)
   intent.putExtra("age", 25)
   startActivity(intent)

   // Receiving
   val age = intent.getIntExtra("age", 0)
   ```

   ---

   ## 🔁 Start Activity for Result (deprecated way and new way)

   ### Old (deprecated):

   ```kotlin
   startActivityForResult(intent, REQUEST_CODE)
   ```

   ### New (recommended with Activity Result API):

   ```kotlin
   val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
       if (result.resultCode == Activity.RESULT_OK) {
           val data = result.data?.getStringExtra("result")
           textView.text = data
       }
   }

   // Launch
   val intent = Intent(this, SecondActivity::class.java)
   launcher.launch(intent)
   ```

   And in `SecondActivity`:

   ```kotlin
   val resultIntent = Intent()
   resultIntent.putExtra("result", "Hello back!")
   setResult(Activity.RESULT_OK, resultIntent)
   finish()
   ```

   ---

   ## 🧠 Summary

   | Use Case                         | Intent Type | Example                                                |
   | -------------------------------- | ----------- | ------------------------------------------------------ |
   | Start another screen in your app | Explicit    | `Intent(this, SecondActivity::class.java)`             |
   | Open a URL                       | Implicit    | `Intent(Intent.ACTION_VIEW, Uri.parse("https://..."))` |
   | Share text                       | Implicit    | `Intent(Intent.ACTION_SEND)`                           |
   | Send email                       | Implicit    | `Intent(Intent.ACTION_SENDTO)`                         |
   | Get result back                  | Explicit    | Activity Result API                                    |

   ---

   Would you like me to show a **simple two-activity app example** (MainActivity → SecondActivity with data passing and result return)? It’s a very practical way to solidify your understanding.


-> a slash plus asterisk will still become a multi-line comment

/*
* so /* will make the lines after the comment section a comment
*
*/

-> The pair composable: Pair takes two generic type parameters. In this case, they're both of type Int.
-> The divider composable
-> reusing entire screens
-> val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
-> al calendar = Calendar.getInstance()

-> /**
    * The Navigation consists of three main parts:
    * 1. NavController: Responsible for navigating between destinations, or screens in the app
    * 2. NavGraph: Maps composable destinations to navigate to
    * 3. NavHost: acts as a container for displaying the current destination of the NavGraph
    */

   /**
    *
    * NavHost(
    *      navController, // an instance of the NavHostController used to navigate between screens; e.g calling the navigate() method
    *      startDestination, // default route, in this case the Start route
    *      modifier,
    * ) {
    *    ======================
    *    |      content       |
    *    =====================
    * }
    */

   /**
    * intent: a request for the system to perform some action, commonly presenting a new activity; in our case we will use the ACTION_SEND activity
    * how to create an intent:
    * 1. Create an intent object and specify the intent, such as ACTION_SEND
    * 2. Specify the type of additional data being sent with the intent. For a simple piece of text you can use "text/plain", though other types, such as "image/" or "video/" are available
    * 3. Pass any additional data to the intent, such as the the text or image to share, by calling the putExtra() method. This intent will take two extras: EXTRA_SUBJECT and EXTRA_TEXT
    * 4. Call the startActivity() method of context, passing in an activity created from the intent
    */

-> the composable() function
-> purpose of val context = LocalContext.current???
-> why some methods use viewModel to get accessed, and some use the state
-> does the back button automagically go back to the previous screen in the stack?
-> how to share an screen shot of the app to using intent?
-> navController.currentBackStackEntryAsState(): currentBackStackEntryAsState() returns a State wrapping the current NavBackStackEntry? (nullable), which represents the latest entry in the navigation back stack.
    -> Because it returns a Compose State, composables that read this value will automatically recompose when the navigation back stack changes (e.g., when navigating between screens).
    -> This is useful to get information about the current destination's route or arguments inside a composable, enabling dynamic UI adjustments based on the current screen.
    -> useages: showing current screen's title, conditional navigation, Synchronizing Navigation State with Other UI Components - Useful in multi-screen apps with Bottom Navigation or Navigation Drawers where the active tab or drawer item should reflect the current screen.
-> navController.popBackStack(CupcakeScreen.Start.name, inclusive = false): used to remove destinations from the navigation back stack, effectively navigating back to a previous screen
    -> pops the current destination off the back stack and navigates aback to the previous destination
    -> When called without arguments, navController.popBackStack() pops the current destination off the back stack and navigates back to the previous destination
    -> you can also specify a particular destination route or ID to pop back to , removing all destinations above it on the stack
    -> it returns a Boolean indicating whether the pop action was successfull
    -> the inclusive argument controls whether to also remove the specified destination ("StartScreen") itself
        -> pop everything above the target destination but leave the target destination itself on the stack (you navigate back to it)

-> fun shareOrder(context: Context, subject: String, summary: String) {
           val intent = Intent(Intent.ACTION_SEND).apply { // creates an Intent with action Intent.ACTION_SEND, which indicates that the app wants to send data to another app
               type = "text/plain" // The MIME type is set to "text/plain" to specify that the data being shared in plain text
               putExtra(Intent.EXTRA_SUBJECT, subject) // putting extra data: the title or subject
               putExtra(Intent.EXTRA_TEXT, summary) // putting extra data: is set to the summary parameter, which contains the main text content to share
           }
            val chooser = Intent.createChooser(intent, context.getString(R.string.new_cupcake_order)) // wraps the intent, allowing the user to pick their preferred app to share the contentwith.
            context.startActivity(chooser)
       }



    -> specifying custome apps as Intents:
    fun showCustomShareChooser(context: Context, textToShare: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textToShare)
        }

        // Query PackageManager for apps that can handle ACTION_SEND
        val pm = context.packageManager
        val resInfoList = pm.queryIntentActivities(sendIntent, 0)

        // Filter your apps of interest by package name or other criteria
        val targetedShareIntents = ArrayList<Intent>()
        for (resInfo in resInfoList) {
            val packageName = resInfo.activityInfo.packageName
            // Example: only allow WhatsApp and Messenger as options
            if (packageName.contains("com.whatsapp") || packageName.contains("com.facebook.orca")) {
                val targetedIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, textToShare)
                    setPackage(packageName)
                }
                targetedShareIntents.add(targetedIntent)
            }
        }

        // Create a chooser with initial intents as your filtered list
        val chooserIntent = Intent.createChooser(targetedShareIntents.removeAt(0), "Share via")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toTypedArray())

        context.startActivity(chooserIntent)
    }




