const std = @import("std");
const clap = @import("clap");

const io = std.io;

const Options = struct {
    words: bool = true,
    lines: bool = true,
    chars: bool = true,
};

const whitespaces = [6]u8{ 9, 10, 11, 12, 13, 32 };

pub fn main() !void {
    var words: usize = 0;
    var chars: usize = 0;
    var lines: usize = 0;
    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    defer _ = gpa.deinit();

    const params = comptime clap.parseParamsComptime(
        \\-w,  --words      print word count
        \\-c,  --chars      print char count
        \\-l,  --lines      print line count
        \\<str>
    );

    var diag = clap.Diagnostic{};
    var res = clap.parse(clap.Help, &params, clap.parsers.default, .{
        .diagnostic = &diag,
        .allocator = gpa.allocator(),
    }) catch |err| {
        diag.report(io.getStdErr().writer(), err) catch {};
        return err;
    };
    defer res.deinit();

    const file = try std.fs.cwd().openFile(res.positionals[0], .{});
    defer file.close();
    var buffered_file = std.io.bufferedReader(file.reader());
    var buffer: [1]u8 = undefined;
    var preWhite: bool = false;
    while (true) {
        const number_of_read_bytes = try buffered_file.read(&buffer);
        chars = chars + 1;

        if (number_of_read_bytes == 0) {
            words = words + 1;
            break;
        }

        if (std.mem.containsAtLeast(u8, &whitespaces, 1, &[_]u8{buffer[0]})) {
            preWhite = true;
        } else {
            words = words + 1;
            preWhite = false;
        }

        if (buffer[0] == 10) {
            lines = lines + 1;
        }
    }

    if (res.args.words == 1) {
        std.debug.print("{d}\t", .{words});
    }
    if (res.args.lines == 1) {
        std.debug.print("{d}\t", .{lines});
    }
    if (res.args.chars == 1) {
        std.debug.print("{d}\t", .{chars});
    }
}
