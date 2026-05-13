import org.gradle.api.tasks.javadoc.Javadoc

plugins {
    base
}

val outputDir = layout.buildDirectory.dir("docs/aggregateJavadoc")

tasks.register<Copy>("aggregateJavadoc") {
    description = "collect each module's javadoc into one site"
    group = "documentation"

    into(outputDir)

    subprojects.forEach { sub ->
        sub.plugins.withId("java") {
            from(sub.tasks.named<Javadoc>("javadoc")) {
                into(sub.name)
            }
        }
    }

    doLast {
        val modules = subprojects
            .filter { it.plugins.hasPlugin("java") }
            .map { it.name }
            .sorted()
        outputDir.get().file("index.html").asFile.writeText(renderIndex(modules))
    }
}

fun renderIndex(modules: List<String>): String {
    val items = modules.joinToString("\n      ") {
        """<li><a href="$it/index.html">$it</a></li>"""
    }
    return """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
          <meta charset="UTF-8">
          <title>Aggregated Javadoc</title>
          <style>
            * { box-sizing: border-box; }
            body {
              font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", system-ui, sans-serif;
              margin: 0;
              background: #f6f8fa;
              color: #1f2328;
              line-height: 1.5;
            }
            .container { max-width: 720px; margin: 0 auto; padding: 64px 24px; }
            h1 { font-size: 28px; margin: 0 0 8px; letter-spacing: -0.02em; }
            .subtitle { color: #656d76; margin: 0 0 32px; font-size: 14px; }
            ul { list-style: none; padding: 0; margin: 0; display: grid; gap: 12px; }
            li a {
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding: 16px 20px;
              background: #fff;
              border: 1px solid #d0d7de;
              border-radius: 8px;
              text-decoration: none;
              color: #0969da;
              font-weight: 500;
              transition: border-color .15s, background .15s, transform .15s;
            }
            li a::after { content: "→"; color: #8c959f; }
            li a:hover { border-color: #0969da; background: #ddf4ff; transform: translateY(-1px); }
          </style>
        </head>
        <body>
          <div class="container">
            <h1>Aggregated Javadoc</h1>
            <p class="subtitle">Select a module to browse its Javadoc.</p>
            <ul>
              $items
            </ul>
          </div>
        </body>
        </html>
    """.trimIndent()
}
