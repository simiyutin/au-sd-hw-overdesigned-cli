package com.au.yaveyn.cli.commands

import com.au.yaveyn.cli.State
import com.au.yaveyn.cli.streams.CommandInputStream
import com.au.yaveyn.cli.streams.CommandOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream


class LsCommand: Command() {

    companion object {
        /**
         * Maximum number of parameters.
         */
        val maxNumberOfParams: Int? = 1
    }

    override val name: String = "ls"

    override fun run(state: State, input: CommandInputStream?, output: CommandOutputStream) {
        val paths = Files.walk(Paths.get(state.getPath()), 1)
        val strings = mutableListOf<String>()
        paths.forEach({path -> filteredPrint(path.toAbsolutePath().toString(), state.getPath(), strings)})
        strings.sort()
        val last = strings.removeAt(strings.size - 1)
        strings.forEach{string -> output.writeln(string)}
        output.write(last)
    }

    private fun filteredPrint(path: String, absolute: String, strings: MutableList<String>) {
        val pattern = if(absolute == "/") "(?<=$absolute).*" else "(?<=$absolute/).*"
        val regex = Regex(pattern)
        val res = regex.find(path)
        if (res != null) {
            val out = res.groups[0]?.value
            if (out != null && !out.startsWith(".") && out != "") {
                strings.add(out)
            }
        }
    }
}