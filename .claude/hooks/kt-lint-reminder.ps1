$stdinJson = [Console]::In.ReadToEnd() | ConvertFrom-Json
$path = $stdinJson.tool_input.file_path
if ($path -and ($path -match '\.(kt|kts)$')) {
    $result = @{
        systemMessage = 'Kotlin file changed - consider running gradlew.bat lint before committing.'
    }
    $result | ConvertTo-Json -Compress -Depth 5
}
