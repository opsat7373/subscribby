$stdinJson = [Console]::In.ReadToEnd() | ConvertFrom-Json
$path = $stdinJson.tool_input.file_path
if ($path -and ($path -match 'app[\\/]build[\\/]' -or $path -match '\.gradle[\\/]')) {
    $result = @{
        hookSpecificOutput = @{
            hookEventName = 'PreToolUse'
            permissionDecision = 'deny'
            permissionDecisionReason = 'Gradle-generated file (app/build or .gradle) - do not hand-edit generated artifacts'
        }
    }
    $result | ConvertTo-Json -Compress -Depth 5
}
