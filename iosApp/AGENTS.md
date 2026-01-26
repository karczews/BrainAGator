# IOS APP MODULE

**Purpose**: iOS application entry point embedding Compose Multiplatform framework

## STRUCTURE
```
iosApp/
├── iosApp/                   # Main iOS project directory
│   ├── iOSApp.swift          # SwiftUI entry point
│   ├── ContentView.swift       # Embeds Compose framework
│   └── Assets.xcassets/     # App icons and resources
├── Configuration/
│   └── Config.xcconfig       # iOS build configuration
└── iosApp.xcodeproj/        # Xcode project
```

## WHERE TO LOOK
| Task | Location |
|------|----------|
| App entry | iosApp/iOSApp.swift |
| Compose embedding | iosApp/ContentView.swift |
| Build config | Configuration/Config.xcconfig |
| Resources | iosApp/Assets.xcassets/ |

## CONVENTIONS
- **Entry Point**: SwiftUI App struct in iOSApp.swift
- **Compose Integration**: ContentView.swift embeds Compose framework from composeApp
- **Bundle ID**: `com.example.brainagator.brainagator$(TEAM_ID)`
- **Version**: MARKETING_VERSION=1.0, CURRENT_PROJECT_VERSION=1

## NOTES
- Uses SwiftUI app shell wrapping Compose framework
- Compose framework built from composeApp module (iosArm64, iosSimulatorArm64 targets)
- Open in Xcode for iOS-specific development and debugging
